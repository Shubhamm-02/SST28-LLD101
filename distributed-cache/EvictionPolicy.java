/**
 * Strategy interface for cache eviction.
 * Pluggable: implement this to add LFU, MRU, FIFO, etc.
 */
public interface EvictionPolicy<K> {

    /** Called whenever a key is accessed (get or put). */
    void keyAccessed(K key);

    /** Returns the key that should be evicted next. */
    K evict();

    /** Remove tracking info for a key (e.g. after explicit delete). */
    void remove(K key);
}
