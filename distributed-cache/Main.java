/**
 * Demo driver to exercise the distributed cache.
 */
public class Main {

    public static void main(String[] args) {
        // -- Setup --
        Database db = new InMemoryDatabase();
        // pre-populate some data in the "database"
        db.put("user:1", "Alice");
        db.put("user:2", "Bob");
        db.put("user:3", "Charlie");
        db.put("user:4", "Diana");
        db.put("user:5", "Eve");

        // 3 cache nodes, capacity 2 each, modulo distribution
        DistributedCache cache = new DistributedCache(
                3, 2,
                new ModuloDistributionStrategy(),
                db
        );

        System.out.println("=== Cache GET (all misses — will read from DB) ===");
        System.out.println("user:1 → " + cache.get("user:1"));
        System.out.println("user:2 → " + cache.get("user:2"));
        System.out.println("user:3 → " + cache.get("user:3"));

        System.out.println("\n=== Cache GET (should be hits now) ===");
        System.out.println("user:1 → " + cache.get("user:1"));
        System.out.println("user:2 → " + cache.get("user:2"));

        System.out.println("\n=== PUT more keys to trigger eviction (capacity=2 per node) ===");
        cache.put("user:4", "Diana");
        cache.put("user:5", "Eve");
        cache.put("user:6", "Frank");

        System.out.println("\n=== GET after eviction ===");
        System.out.println("user:1 → " + cache.get("user:1"));
        System.out.println("user:4 → " + cache.get("user:4"));
        System.out.println("user:6 → " + cache.get("user:6"));
    }
}
