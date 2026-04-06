# Distributed Cache — Low Level Design

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
│ - distributionStrategy : DistributionS.│
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

## How Data is Distributed Across Nodes

The `DistributedCache` holds a list of `CacheNode` objects (configurable count). When `get(key)` or `put(key, value)` is called, the cache delegates to a **`DistributionStrategy`** to decide which node owns the key:

```java
int index = distributionStrategy.getNodeIndex(key, nodes.size());
CacheNode node = nodes.get(index);
```

The current implementation uses **`ModuloDistributionStrategy`**:

```
nodeIndex = Math.abs(key.hashCode()) % totalNodes
```

This ensures deterministic routing — the same key always maps to the same node.

---

## How Cache Miss is Handled

On a `get(key)`:

1. Route to the correct `CacheNode` using the distribution strategy.
2. If the node **has** the key → return value (cache hit).
3. If the node **does not** have the key → **read-through** from the `Database`, store the value in the cache node, then return it.

```
Client → DistributedCache.get("user:1")
            │
            ├── route to Node-2  (via distribution strategy)
            ├── Node-2.get("user:1") → null  (MISS)
            ├── Database.get("user:1") → "Alice"
            ├── Node-2.put("user:1", "Alice")
            └── return "Alice"
```

---

## How Eviction Works

Each `CacheNode` has a fixed **capacity**. When a `put` would exceed capacity:

1. The node asks its `EvictionPolicy` for the key to evict (`evict()`).
2. That key is removed from the node's internal store.
3. The new key-value pair is then inserted.

With **LRU (Least Recently Used)**:
- Every `get` or `put` call on a key marks it as "recently used" (`keyAccessed`).
- `LRUEvictionPolicy` uses a `LinkedHashMap` in **access-order** mode — the head of the map is always the least-recently-used entry.
- On eviction, the head entry is removed and its key returned.

---

## Extensibility

### Plugging a Different Distribution Strategy

Implement `DistributionStrategy` and pass it to `DistributedCache`:

```java
public class ConsistentHashStrategy implements DistributionStrategy {
    @Override
    public int getNodeIndex(String key, int totalNodes) {
        // consistent hashing logic here
    }
}
```

### Plugging a Different Eviction Policy

Implement `EvictionPolicy<K>` and use it when constructing cache nodes:

```java
public class LFUEvictionPolicy<K> implements EvictionPolicy<K> {
    // track access frequency per key
    @Override public void keyAccessed(K key) { /* increment count */ }
    @Override public K evict() { /* return key with lowest frequency */ }
    @Override public void remove(K key) { /* cleanup */ }
}
```

To wire it in, modify `DistributedCache` to accept an eviction policy factory (or simply change the constructor loop).

---

## Files

| File | Role |
|------|------|
| `EvictionPolicy.java` | Interface — pluggable eviction strategy |
| `LRUEvictionPolicy.java` | LRU implementation using LinkedHashMap |
| `DistributionStrategy.java` | Interface — pluggable key distribution |
| `ModuloDistributionStrategy.java` | Modulo-based (hash % n) distribution |
| `Database.java` | Interface — backing data store |
| `InMemoryDatabase.java` | Simple HashMap-based DB stub |
| `CacheNode.java` | Single cache node with capacity + eviction |
| `DistributedCache.java` | Orchestrator — ties everything together |
| `Main.java` | Demo driver |

## Assumptions

- Keys are `String`, values are `String` (kept simple for LLD exercise).
- No real network communication — all nodes are in-memory objects.
- Write-through to DB on `put`; read-through from DB on cache miss.
- Each node gets its own independent `EvictionPolicy` instance.
- The `Database` interface is assumed to be pre-existing.
