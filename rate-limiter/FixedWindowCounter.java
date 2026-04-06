import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Fixed Window Counter rate limiting algorithm.
 *
 * Divides time into fixed-size windows (e.g. every 60 seconds).
 * Counts requests per key in the current window.
 * Resets the counter when the window rolls over.
 *
 * Thread-safe via ConcurrentHashMap + AtomicInteger.
 */
public class FixedWindowCounter implements RateLimiter {

    private final RateLimiterConfig config;

    // key → [windowId, counter]
    private final ConcurrentHashMap<String, WindowEntry> windows;

    public FixedWindowCounter(RateLimiterConfig config) {
        this.config = config;
        this.windows = new ConcurrentHashMap<>();
    }

    @Override
    public boolean allowRequest(String key) {
        long now = System.currentTimeMillis();
        long currentWindowId = now / config.getWindowSizeInMillis();

        WindowEntry entry = windows.compute(key, (k, existing) -> {
            if (existing == null || existing.windowId != currentWindowId) {
                // new window — reset counter
                return new WindowEntry(currentWindowId, new AtomicInteger(0));
            }
            return existing;
        });

        int count = entry.counter.incrementAndGet();
        return count <= config.getMaxRequests();
    }

    /** Internal holder for a window's ID and its request counter. */
    private static class WindowEntry {
        final long windowId;
        final AtomicInteger counter;

        WindowEntry(long windowId, AtomicInteger counter) {
            this.windowId = windowId;
            this.counter = counter;
        }
    }
}
