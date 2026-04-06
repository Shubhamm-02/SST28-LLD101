# Distributed Cache — Low Level Design

## Overview

This project implements an in-memory distributed cache that spreads data across multiple cache nodes. It supports `get(key)` and `put(key, value)` operations with **read-through** and **write-through** semantics backed by a database. The design uses the **Strategy Pattern** in two places to keep distribution logic and eviction logic independently pluggable.

---

## Class Diagram

```
┌─────────────────────────┐
│         Main            │  (Demo / Driver)
└────────────┬────────────┘
             │ uses
             ▼
┌─────────────────────────────────────────┐
│          DistributedCache               │
│─────────────────────────────────────────│
│ - nodes : List<CacheNode>              │
│ - distributionStrategy : Distribution. │
│ - database : Database                  │
│─────────────────────────────────────────│
│ + get(key) : String                    │
│ + put(key, value) : void               │
│ - getNode(key) : CacheNode             │
└──────┬──────────┬───────────┬──────────┘
       │          │           │
       ▼          ▼           ▼
  ┌──────────┐ ┌──────────────────┐ ┌──────────────┐
  │CacheNode │ │<<interface>>     │ │<<interface>>  │
  │──────────│ │DistributionStrat.│ │  Database     │
  │-nodeId   │ │──────────────────│ │──────────────│
  │-capacity │ │+getNodeIndex(    │ │+get(key)     │
  │-store    │ │  key, totalNodes)│ │+put(key,val) │
  │-eviction │ └────────┬─────────┘ └──────┬───────┘
  │──────────│          │                  │
  │+get(key) │          ▼                  ▼
  │+put(k,v) │ ┌──────────────────┐ ┌──────────────┐
  └────┬─────┘ │ModuloDistribution│ │InMemoryDB    │
       │       │    Strategy      │ │              │
       ▼       └──────────────────┘ └──────────────┘
  ┌──────────────────┐
  │  <<interface>>   │
  │  EvictionPolicy  │
  │──────────────────│
  │ +keyAccessed(key)│
  │ +evict() : K     │
  │ +remove(key)     │
  └────────┬─────────┘
           │
           ▼
  ┌──────────────────┐
  │LRUEvictionPolicy │
  │──────────────────│
  │-accessOrder :    │
  │ LinkedHashMap    │
  └──────────────────┘
```

---

## File-by-File Implementation Walkthrough

### 1. `DistributionStrategy.java` — Interface

```java
public interface DistributionStrategy {
    int getNodeIndex(String key, int totalNodes);
}
```

This is the **abstraction for key routing**. Given any key and the total number of nodes, it returns the index (`0` to `totalNodes - 1`) of the node that should own the key. By programming to this interface, `DistributedCache` is decoupled from any specific hashing scheme.

---

### 2. `ModuloDistributionStrategy.java` — Concrete Strategy

```java
public int getNodeIndex(String key, int totalNodes) {
    int hash = Math.abs(key.hashCode());
    return hash % totalNodes;
}
```

The simplest distribution approach:
1. Compute Java's built-in `hashCode()` for the key string.
2. Take the absolute value (to avoid negative mod results).
3. Apply `% totalNodes` to get a node index.

**Trade-off:** This is easy to implement but has a problem — if you add or remove nodes, almost every key remaps to a different node (cache invalidation storm). That's why the interface exists: you can swap in a `ConsistentHashStrategy` later without touching any other class.

---

### 3. `EvictionPolicy.java` — Interface

```java
public interface EvictionPolicy<K> {
    void keyAccessed(K key);   // track an access
    K evict();                 // choose and remove victim
    void remove(K key);        // cleanup tracking for a deleted key
}
```

This is the **abstraction for eviction logic**. The generic type `<K>` lets it work with any key type. Three operations:

| Method | Purpose |
|--------|---------|
| `keyAccessed(key)` | Called on every `get` or `put` so the policy can update its internal tracking (recency, frequency, etc.) |
| `evict()` | Returns the key that should be evicted next, and removes it from the policy's internal tracking |
| `remove(key)` | Explicitly removes a key from tracking (e.g., if a key is deleted from the cache externally) |

---

### 4. `LRUEvictionPolicy.java` — Concrete Policy

```java
private final LinkedHashMap<K, Boolean> accessOrder;

public LRUEvictionPolicy() {
    // third argument `true` = access-order mode
    this.accessOrder = new LinkedHashMap<>(16, 0.75f, true);
}
```

**How it works internally:**

Java's `LinkedHashMap` has two modes:
- **Insertion order** (default) — entries stay in the order they were inserted.
- **Access order** (`accessOrder = true`) — every time you `get` or `put` an entry, it moves to the **tail** of the linked list.

This means the **head** of the map is always the **least recently used** entry.

**`keyAccessed(key)`** — Calls `accessOrder.put(key, true)`. If the key already exists, `LinkedHashMap` in access-order mode moves it to the tail (marking it as most recently used). If it's new, it's appended at the tail.

**`evict()`** — Grabs an iterator over the map's keys. The first key returned (`it.next()`) is the head — the LRU key. It removes that entry via `it.remove()` and returns the key.

```java
public K evict() {
    Iterator<K> it = accessOrder.keySet().iterator();
    if (!it.hasNext()) return null;
    K lruKey = it.next();   // head = least recently used
    it.remove();
    return lruKey;
}
```

**Time complexity:** All three operations are **O(1)** because `LinkedHashMap` uses a doubly-linked list internally.

---

### 5. `Database.java` — Interface

```java
public interface Database {
    String get(String key);
    void put(String key, String value);
}
```

Represents the **backing data store** that the cache sits in front of. This is assumed to already exist per the problem statement. The cache uses it for:
- **Read-through:** On cache miss, fetch from `database.get(key)`.
- **Write-through:** On `put`, write to both the cache node and `database.put(key, value)`.

---

### 6. `InMemoryDatabase.java` — Stub Implementation

```java
private final Map<String, String> store = new HashMap<>();
```

A simple `HashMap`-backed implementation for demonstration. In a real system, this would be replaced by a JDBC adapter, a Redis client, or any persistent store.

---

### 7. `CacheNode.java` — Single Cache Node

Each `CacheNode` holds:
- `nodeId` — human-readable identifier (e.g., `"Node-0"`)
- `capacity` — maximum number of entries it can hold
- `store` — a `HashMap<String, String>` for the actual cached data
- `evictionPolicy` — the plugged-in `EvictionPolicy` instance

**`get(key)` flow:**
```
1. Check if key exists in store
2. If NO  → return null (caller handles the cache miss)
3. If YES → call evictionPolicy.keyAccessed(key) to update recency
          → return the value
```

**`put(key, value)` flow:**
```
1. If key already exists in store:
     → Update the value in store
     → Call evictionPolicy.keyAccessed(key)
     → Return (no eviction needed, size unchanged)

2. If key is new AND store.size() >= capacity:
     → Call evictionPolicy.evict() to get the victim key
     → Remove victim from store
     → Log the eviction

3. Insert the new key-value into store
4. Call evictionPolicy.keyAccessed(key)
```

The key insight: the `CacheNode` does **not** know anything about LRU or any specific eviction algorithm. It simply asks the `EvictionPolicy` interface "who should I evict?" and "I just accessed this key." This is what makes the eviction strategy pluggable.

---

### 8. `DistributedCache.java` — The Orchestrator

This is the **central class** that ties everything together. It holds:
- A `List<CacheNode>` — the pool of cache nodes
- A `DistributionStrategy` — decides which node owns a key
- A `Database` — the backing store for cache misses

**Constructor:**
```java
public DistributedCache(int numberOfNodes, int capacityPerNode,
                        DistributionStrategy distributionStrategy,
                        Database database)
```
The number of nodes and capacity per node are configurable. It creates `numberOfNodes` cache nodes in a loop, each with its own `LRUEvictionPolicy` instance.

**`get(key)` implementation:**
```
1. Call getNode(key):
     index = distributionStrategy.getNodeIndex(key, nodes.size())
     return nodes.get(index)

2. Try node.get(key)
   
3. If result is null (CACHE MISS):
     → Fetch value from database.get(key)
     → If found, store in node: node.put(key, value)
     → Return the value (or null if not in DB either)

4. If result is non-null (CACHE HIT):
     → Return the value directly
```

**`put(key, value)` implementation:**
```
1. Route to the correct node via getNode(key)
2. Store in the cache node: node.put(key, value)
3. Write-through to DB: database.put(key, value)
```

The private helper `getNode(key)` is called by both `get` and `put` to ensure the same key always maps to the same node:
```java
private CacheNode getNode(String key) {
    int index = distributionStrategy.getNodeIndex(key, nodes.size());
    return nodes.get(index);
}
```

---

### 9. `Main.java` — Demo Driver

The driver sets up a scenario with **3 nodes, capacity 2 each**, and a database pre-populated with 5 users:

```
Phase 1: GET user:1, user:2, user:3
  → All cache misses → fetched from DB → stored in cache

Phase 2: GET user:1, user:2
  → Cache hits (they were cached in Phase 1)

Phase 3: PUT user:4, user:5, user:6
  → New entries go into nodes → may cause eviction since capacity is 2

Phase 4: GET user:1, user:4, user:6
  → Tests whether evicted keys are re-fetched from DB
```

---

## How Data is Distributed Across Nodes

When any `get` or `put` is called, the `DistributedCache` uses the `DistributionStrategy` to pick a node:

```java
int index = distributionStrategy.getNodeIndex(key, nodes.size());
```

With `ModuloDistributionStrategy` and 3 nodes:

| Key | hashCode() | abs(hash) % 3 | Node |
|-----|-----------|----------------|------|
| `"user:1"` | `–1,421,005,089` | `2` | Node-2 |
| `"user:2"` | `–1,421,005,088` | `1` | Node-1 |
| `"user:3"` | `–1,421,005,087` | `0` | Node-0 |

The same key always hashes to the same node, so reads and writes are consistent.

---

## How Cache Miss is Handled

```
Client calls cache.get("user:1")
  │
  ├─ DistributedCache routes to Node-2  (modulo strategy)
  ├─ Node-2.get("user:1") returns null  (not cached yet)
  │
  ├─ DistributedCache fetches from Database:
  │     database.get("user:1") → "Alice"
  │
  ├─ DistributedCache stores in Node-2:
  │     Node-2.put("user:1", "Alice")
  │
  └─ Returns "Alice" to the client
```

On the next `get("user:1")`, Node-2 already has it → **cache hit**, no DB call.

---

## How Eviction Works

Example: Node-2 has capacity 2 and currently holds `{user:1 → Alice, user:4 → Diana}`.

```
cache.put("user:7", "Grace")   // assume user:7 also maps to Node-2
  │
  ├─ Node-2.put("user:7", "Grace")
  │    ├─ store.size() == 2 == capacity → must evict
  │    ├─ evictionPolicy.evict()
  │    │    └─ LinkedHashMap head = "user:1" (least recently used)
  │    │       removes "user:1" from tracking, returns "user:1"
  │    ├─ store.remove("user:1")
  │    ├─ store.put("user:7", "Grace")
  │    └─ evictionPolicy.keyAccessed("user:7")
  │
  └─ Node-2 now holds: {user:4 → Diana, user:7 → Grace}
```

The LRU policy tracks **recency of access**, not insertion order. If `user:1` had been accessed more recently than `user:4`, then `user:4` would be evicted instead.

---

## How the Design Supports Extensibility

### 1. Adding a New Distribution Strategy

Create a new class implementing `DistributionStrategy` and pass it to the constructor:

```java
public class ConsistentHashStrategy implements DistributionStrategy {
    @Override
    public int getNodeIndex(String key, int totalNodes) {
        // consistent hashing with virtual nodes, etc.
    }
}

// Usage:
DistributedCache cache = new DistributedCache(
    3, 100, new ConsistentHashStrategy(), db
);
```

No changes needed in `DistributedCache`, `CacheNode`, or any other class.

### 2. Adding a New Eviction Policy

Create a new class implementing `EvictionPolicy<K>`:

```java
public class LFUEvictionPolicy<K> implements EvictionPolicy<K> {
    private Map<K, Integer> frequency = new HashMap<>();
    
    @Override
    public void keyAccessed(K key) {
        frequency.merge(key, 1, Integer::sum);
    }
    
    @Override
    public K evict() {
        // find and remove key with lowest frequency
    }
    
    @Override
    public void remove(K key) {
        frequency.remove(key);
    }
}
```

Then modify the node creation loop in `DistributedCache` (or better yet, accept a factory/supplier):

```java
// Pass a supplier so each node gets its own policy instance
Supplier<EvictionPolicy<String>> policyFactory = LFUEvictionPolicy::new;
```

No changes needed in `CacheNode` — it only talks to the `EvictionPolicy` interface.

---

## Design Patterns Used

| Pattern | Where | Why |
|---------|-------|-----|
| **Strategy** | `DistributionStrategy` | Decouple key-routing algorithm from the cache orchestrator |
| **Strategy** | `EvictionPolicy` | Decouple eviction algorithm from the cache node |
| **Dependency Injection** | `DistributedCache` constructor | All dependencies (strategy, database) are injected, not created internally |

---

## Assumptions

- Keys and values are `String` (kept simple for an LLD exercise).
- No real network communication — all nodes are in-memory Java objects.
- **Write-through:** `put` writes to both the cache and the database.
- **Read-through:** `get` fetches from the database on a cache miss.
- Each node has its own independent `EvictionPolicy` instance.
- The `Database` interface is assumed to already exist.
- Keys are unique across the system.

---

## How to Run

```bash
cd distributed-cache
javac *.java
java Main
```
