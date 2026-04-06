import java.util.HashMap;
import java.util.Map;

/**
 * In-memory stub database for demonstration purposes.
 */
public class InMemoryDatabase implements Database {

    private final Map<String, String> store = new HashMap<>();

    @Override
    public String get(String key) {
        return store.get(key);
    }

    @Override
    public void put(String key, String value) {
        store.put(key, value);
    }
}
