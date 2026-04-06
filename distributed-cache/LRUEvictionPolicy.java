import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Iterator;

/**
 * Least-Recently-Used eviction policy.
 * Uses a LinkedHashMap in access-order mode to track recency.
 */
public class LRUEvictionPolicy<K> implements EvictionPolicy<K> {

    // access-ordered map: least-recently-used entry is at the head
    private final LinkedHashMap<K, Boolean> accessOrder;

    public LRUEvictionPolicy() {
        this.accessOrder = new LinkedHashMap<>(16, 0.75f, true);
    }

    @Override
    public void keyAccessed(K key) {
        accessOrder.put(key, Boolean.TRUE);   // moves key to tail (most recent)
    }

    @Override
    public K evict() {
        Iterator<K> it = accessOrder.keySet().iterator();
        if (!it.hasNext()) return null;
        K lruKey = it.next();   // head = least-recently-used
        it.remove();
        return lruKey;
    }

    @Override
    public void remove(K key) {
        accessOrder.remove(key);
    }
}
