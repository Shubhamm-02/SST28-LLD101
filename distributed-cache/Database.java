/**
 * Simple interface representing a backing data store.
 * In a real system this would talk to an actual DB.
 */
public interface Database {
    String get(String key);
    void put(String key, String value);
}
