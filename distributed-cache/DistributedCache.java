import java.util.ArrayList;
import java.util.List;

/**
 * Orchestrator that manages multiple CacheNodes, a distribution strategy,
 * and a backing database for cache-miss handling.
 */
public class DistributedCache {

    private final List<CacheNode> nodes;
    private final DistributionStrategy distributionStrategy;
    private final Database database;

    public DistributedCache(int numberOfNodes, int capacityPerNode,
                            DistributionStrategy distributionStrategy,
                            Database database) {
        this.distributionStrategy = distributionStrategy;
        this.database = database;
        this.nodes = new ArrayList<>();

        for (int i = 0; i < numberOfNodes; i++) {
            EvictionPolicy<String> policy = new LRUEvictionPolicy<>();
            nodes.add(new CacheNode("Node-" + i, capacityPerNode, policy));
        }
    }

    /**
     * Get a value by key.
     * On cache miss, fetches from the database, stores in cache, then returns.
     */
    public String get(String key) {
        CacheNode node = getNode(key);
        String value = node.get(key);

        if (value == null) {
            // Cache miss — read-through from DB
            System.out.println("[Cache MISS] key=" + key + " → fetching from DB");
            value = database.get(key);
            if (value != null) {
                node.put(key, value);
            }
        } else {
            System.out.println("[Cache HIT]  key=" + key + " on " + node.getNodeId());
        }

        return value;
    }

    /**
     * Put a key-value pair into the appropriate cache node.
     * Also writes through to the database.
     */
    public void put(String key, String value) {
        CacheNode node = getNode(key);
        node.put(key, value);
        database.put(key, value);   // write-through
        System.out.println("[PUT] key=" + key + " → " + node.getNodeId());
    }

    private CacheNode getNode(String key) {
        int index = distributionStrategy.getNodeIndex(key, nodes.size());
        return nodes.get(index);
    }
}
