/**
 * Simple modulo-based distribution: hash(key) % numberOfNodes.
 */
public class ModuloDistributionStrategy implements DistributionStrategy {

    @Override
    public int getNodeIndex(String key, int totalNodes) {
        int hash = Math.abs(key.hashCode());
        return hash % totalNodes;
    }
}
