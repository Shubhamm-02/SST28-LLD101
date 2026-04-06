# Rate Limiter — Low Level Design

## Overview

This project implements a **pluggable rate limiting system** that controls access to a paid external resource. The rate limiter is **not** applied at the client API level — it is enforced **only** at the point where the system is about to call the external resource.

**Key idea:** Not every incoming API request triggers an external call. Business logic runs first; the rate limiter is consulted only when the external resource is actually needed.

---

## Class Diagram

```
  Client Request
       │
       ▼
┌──────────────────┐
│  InternalService  │   Runs business logic first
│──────────────────│
│+handleRequest()  │
│-requiresExternal │   Decides if external call is needed
│  Resource()      │
└────────┬─────────┘
         │ (only if external call needed)
         ▼
┌──────────────────────────┐
│  ExternalResourceGateway │   Single enforcement point
│──────────────────────────│
│ - rateLimiter: RateLimiter│
│ - externalResource       │
│──────────────────────────│
│ + call(key, request)     │
└──────┬──────────┬────────┘
       │          │
       ▼          ▼
┌──────────────┐ ┌──────────────────┐
│<<interface>> │ │  <<interface>>   │
│ RateLimiter  │ │ ExternalResource │
│──────────────│ │──────────────────│
│+allowRequest │ │ +call(request)   │
│  (key)       │ └────────┬─────────┘
└──────┬───────┘          │
       │                  ▼
       ├──────────┐  ┌────────────────┐
       ▼          ▼  │ PaidExternalAPI │
┌────────────┐ ┌──────────────────┐  └────────────────┘
│FixedWindow │ │SlidingWindow     │
│ Counter    │ │ Counter          │
│────────────│ │──────────────────│
│-windows:   │ │-entries:         │
│ ConcurrentH│ │ ConcurrentHashMap│
└────────────┘ └──────────────────┘
       ▲              ▲
       │              │
       └──────┬───────┘
              │
     ┌────────────────┐
     │RateLimiterConfig│
     │────────────────│
     │-maxRequests    │
     │-windowSizeMs   │
     └────────────────┘
```

---

## File-by-File Implementation Walkthrough

### 1. `RateLimiter.java` — Core Interface

```java
public interface RateLimiter {
    boolean allowRequest(String key);
}
```

This is the **single method** that all rate limiting algorithms must implement. The `key` parameter is flexible — it can represent a customer ID, tenant, API key, or external provider. This allows the same rate limiter to be used in different contexts without changes.

**Why a single method?** The caller (gateway) only needs to know one thing: "is this request allowed?" All the complexity of windowing, counting, and tracking is hidden inside the implementation.

---

### 2. `RateLimiterConfig.java` — Configuration

```java
public class RateLimiterConfig {
    private final int maxRequests;            // e.g. 100
    private final long windowSizeInMillis;     // e.g. 60_000 (1 minute)
}
```

An **immutable** configuration object that holds:
- `maxRequests` — how many requests are allowed per window (e.g., 5, 100, 1000)
- `windowSizeInMillis` — the duration of the window in milliseconds

Examples:
| Limit | maxRequests | windowSizeInMillis |
|-------|-------------|-------------------|
| 5 per minute | 5 | 60,000 |
| 100 per minute | 100 | 60,000 |
| 1000 per hour | 1000 | 3,600,000 |

**Why a separate config class?** It decouples the "what are the limits" from the "how do we enforce them." Both algorithms accept the same config object, making it easy to swap algorithms without changing configuration.

---

### 3. `FixedWindowCounter.java` — Algorithm #1

**How it works:**

Time is divided into **fixed, non-overlapping windows**. For example, with a 60-second window starting at time 0:

```
Window 0: [0s  - 60s)   → counter for this window
Window 1: [60s - 120s)  → counter resets
Window 2: [120s - 180s) → counter resets
```

Each key gets its own counter per window. When a request comes in:

1. **Compute the current window ID:** `windowId = currentTimeMs / windowSizeMs`
2. **Check if we're in a new window:** If yes, reset the counter to 0.
3. **Increment and check:** If `counter <= maxRequests`, allow; otherwise deny.

**Internal data structure:**
```
ConcurrentHashMap<String, WindowEntry>
                           │
                    ┌──────┴──────┐
                    │ windowId: 5 │
                    │ counter: 3  │
                    └─────────────┘
```

**Thread safety:** Uses `ConcurrentHashMap.compute()` which is **atomic** — the lambda that checks and resets the window runs under the key's internal lock. The `AtomicInteger` counter is also thread-safe for `incrementAndGet()`.

```java
WindowEntry entry = windows.compute(key, (k, existing) -> {
    if (existing == null || existing.windowId != currentWindowId) {
        return new WindowEntry(currentWindowId, new AtomicInteger(0));
    }
    return existing;
});
int count = entry.counter.incrementAndGet();
return count <= config.getMaxRequests();
```

**Trade-offs:**
- ✅ Simple to implement and understand
- ✅ O(1) time and space per request
- ❌ **Boundary burst problem:** A user can make `maxRequests` at the end of window N and `maxRequests` at the start of window N+1, effectively getting 2× the limit in a short burst

---

### 4. `SlidingWindowCounter.java` — Algorithm #2

**How it works:**

This algorithm **smooths out** the boundary problem of fixed windows by considering a weighted combination of the previous window's count and the current window's count.

**Formula:**
```
effectiveCount = (previousWindowCount × overlapFraction) + currentWindowCount
```

Where `overlapFraction = 1 - (elapsedTimeInCurrentWindow / windowSize)`

**Visual example:**

```
           Previous Window          Current Window
          ┌──────────────────┐┌──────────────────┐
          │    10 requests   ││   3 requests      │
          └──────────────────┘└──────────────────┘
                              ▲         ▲
                              │         │ now (40% into window)
                              window start

overlapFraction = 1 - 0.4 = 0.6
effectiveCount = (10 × 0.6) + 3 = 6 + 3 = 9
```

So even though the current window only has 3 requests, the system accounts for 60% of the previous window's traffic (because that portion overlaps with a hypothetical sliding window ending at "now").

**Internal data structure:**
```
ConcurrentHashMap<String, SlidingEntry>
                             │
                    ┌────────┴─────────┐
                    │ currentWindowId:5 │
                    │ currentCount: 3   │
                    │ previousCount: 10 │
                    └──────────────────┘
```

**Thread safety:** Uses `synchronized(entry)` for per-key locking. We need the entire read-check-increment operation to be atomic per key, since it involves multiple fields (currentCount, previousCount, currentWindowId).

```java
synchronized (entry) {
    // shift windows if needed
    if (entry.currentWindowId != currentWindowId) {
        entry.previousCount = (entry.currentWindowId == currentWindowId - 1)
                              ? entry.currentCount.get() : 0;
        entry.currentCount.set(0);
        entry.currentWindowId = currentWindowId;
    }

    double overlapFraction = 1.0 - ((double) elapsedInWindow / windowSize);
    double effectiveCount = (entry.previousCount * overlapFraction)
                          + entry.currentCount.get();

    if (effectiveCount >= config.getMaxRequests()) return false;

    entry.currentCount.incrementAndGet();
    return true;
}
```

**Trade-offs:**
- ✅ Much smoother rate limiting — no boundary burst problem
- ✅ Still O(1) time and space per request
- ✅ Only needs 2 counters per key (memory efficient vs. sliding log)
- ❌ Slightly more complex than fixed window
- ❌ Approximate (not exact) — it uses a weighted estimate, not precise per-request timestamps

---

### 5. `ExternalResource.java` — Interface

```java
public interface ExternalResource {
    String call(String request);
}
```

Represents the **paid external API** that we're protecting with rate limiting. Abstractions allow us to stub it for testing or swap providers.

---

### 6. `PaidExternalAPI.java` — Stub Implementation

A simple stub that simulates a paid external call by printing a message. In a real system, this would be an HTTP client calling a third-party API.

---

### 7. `ExternalResourceGateway.java` — Enforcement Point

```java
public class ExternalResourceGateway {
    private final ExternalResource externalResource;
    private final RateLimiter rateLimiter;

    public String call(String key, String request) {
        if (!rateLimiter.allowRequest(key)) {
            // DENIED — return null (caller handles gracefully)
            return null;
        }
        return externalResource.call(request);
    }
}
```

This is **the single point where rate limiting is enforced**. Internal services never call `ExternalResource` directly — they always go through the gateway. This ensures:

1. Rate limiting logic is centralized (not scattered across services).
2. The rate limiter can be swapped without touching any service code.
3. The external resource can be swapped independently of rate limiting.

**The key is passed in by the caller**, making the gateway agnostic to what the key represents (customer, tenant, etc.).

---

### 8. `InternalService.java` — Business Logic Layer

```java
public void handleRequest(String customerId, String requestData) {
    // Business logic first
    if (!requiresExternalResource(requestData)) {
        // Serve from internal data — NO rate limiting check
        return;
    }

    // Only NOW do we consult the rate limiter (via gateway)
    String response = gateway.call(customerId, requestData);

    if (response != null) {
        // Use the response
    } else {
        // Rate limited — return fallback
    }
}
```

**Critical design point:** The rate limiter is not on the incoming API. It's consulted only when the service determines that an external call is actually needed. Requests that don't need the external resource don't consume quota at all.

---

### 9. `Main.java` — Demo Driver

Demonstrates three scenarios:

| Scenario | What Happens |
|----------|-------------|
| Internal-only requests | `requiresExternalResource()` returns false → no rate limit check → served immediately |
| 7 external requests for customer T1 (limit=5) | First 5 succeed, requests 6 & 7 are denied |
| External requests for customer T2 | Allowed independently — T2 has its own quota |

**Switching algorithms** is a one-line change:
```java
// Just change this line — nothing else changes
RateLimiter limiter = new FixedWindowCounter(config);
// RateLimiter limiter = new SlidingWindowCounter(config);
```

---

## Request Flow Diagram

```
Client API Request
       │
       ▼
InternalService.handleRequest()
       │
       ├── requiresExternalResource()?
       │       │
       │       ├── NO → serve from internal data (no rate limiting)
       │       │
       │       └── YES ──► ExternalResourceGateway.call(key, request)
       │                          │
       │                    rateLimiter.allowRequest(key)?
       │                          │
       │                     ┌────┴────┐
       │                     │         │
       │                   YES        NO
       │                     │         │
       │                     ▼         ▼
       │              ExternalResource   return null
       │              .call(request)     (rate limited)
       │                     │
       │                     ▼
       │              return response
       │
       ▼
  Return to client
```

---

## Algorithm Comparison

| Aspect | Fixed Window Counter | Sliding Window Counter |
|--------|---------------------|----------------------|
| **Accuracy** | Can allow 2× burst at window boundary | Smooth — no boundary burst |
| **Complexity** | Very simple | Slightly more complex |
| **Memory per key** | 1 counter + 1 window ID | 2 counters + 1 window ID |
| **Time per check** | O(1) | O(1) |
| **Thread safety** | `ConcurrentHashMap.compute()` | `synchronized` per key |
| **Best for** | Simple use cases, non-critical limits | Production systems needing smooth enforcement |

### The Boundary Burst Problem (Fixed Window)

```
Window N                    Window N+1
├──────────────────┤├──────────────────┤
                 ▲▲▲▲▲            
              5 requests at      5 requests at
              end of window      start of window
              
Result: 10 requests in ~2 seconds despite a limit of 5/minute!
```

Sliding window prevents this by weighting the previous window's count.

---

## Design Decisions

1. **Interface-based design (`RateLimiter`)** — Allows plugging in any algorithm (Token Bucket, Leaky Bucket, etc.) without touching business logic or the gateway.

2. **Gateway pattern (`ExternalResourceGateway`)** — Centralizes rate limiting enforcement in one place instead of scattering checks across services.

3. **Rate limiting key as a parameter** — The gateway doesn't decide what the key is. The caller passes it, so the same gateway can rate-limit by customer, tenant, API key, or provider depending on the use case.

4. **Config object (`RateLimiterConfig`)** — Separates "what limits" from "how to enforce" — both algorithms use the same config.

5. **Thread safety** — Both implementations use concurrent data structures suitable for multi-threaded server environments.

---

## How to Add a New Algorithm

Implement the `RateLimiter` interface:

```java
public class TokenBucketLimiter implements RateLimiter {
    @Override
    public boolean allowRequest(String key) {
        // token bucket logic
    }
}
```

Then swap it in at construction time:
```java
RateLimiter limiter = new TokenBucketLimiter(config);
```

Zero changes in `ExternalResourceGateway`, `InternalService`, or any other class.

---

## Assumptions

- This is an in-memory LLD exercise — no distributed state across multiple server instances.
- The `ExternalResource` interface represents a pre-existing paid API.
- The rate-limiting key is determined by the caller (not by the rate limiter itself).
- Keys are unique per entity (customer, tenant, etc.).

---

## How to Run

```bash
cd rate-limiter
javac *.java
java Main
```
