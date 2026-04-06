/**
 * Strategy interface for distributing keys across cache nodes.
 * Pluggable: implement this to add consistent hashing, map-based routing, etc.
 */
public interface DistributionStrategy {

    /**
     * Given a key and the total number of nodes, return the index of the
     * node that should own this key.
     */
    int getNodeIndex(String key, int totalNodes);
}
