import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Sliding Window Counter rate limiting algorithm.
 *
 * Combines the count from the previous fixed window (weighted by overlap)
 * with the count from the current window to approximate a true sliding window.
 *
 * Formula:
 *   effectiveCount = (prevCount * overlapFraction) + currentCount
 *
 * where overlapFraction = 1 - (elapsedInCurrentWindow / windowSize)
 *
 * Thread-safe via ConcurrentHashMap + synchronized per-key access.
 */
public class SlidingWindowCounter implements RateLimiter {

    private final RateLimiterConfig config;

    // key → sliding window entry (holds current + previous window data)
    private final ConcurrentHashMap<String, SlidingEntry> entries;

    public SlidingWindowCounter(RateLimiterConfig config) {
        this.config = config;
        this.entries = new ConcurrentHashMap<>();
    }

    @Override
    public boolean allowRequest(String key) {
        long now = System.currentTimeMillis();
        long windowSize = config.getWindowSizeInMillis();
        long currentWindowId = now / windowSize;

        SlidingEntry entry = entries.computeIfAbsent(key, k -> new SlidingEntry());

        synchronized (entry) {
            // if we've moved to a new window, shift current → previous
            if (entry.currentWindowId != currentWindowId) {
                if (entry.currentWindowId == currentWindowId - 1) {
                    // previous window is the one we just left
                    entry.previousCount = entry.currentCount.get();
                } else {
                    // we skipped one or more windows — previous is stale
                    entry.previousCount = 0;
                }
                entry.currentCount.set(0);
                entry.currentWindowId = currentWindowId;
            }

            // calculate weighted count
            long windowStart = currentWindowId * windowSize;
            long elapsedInWindow = now - windowStart;
            double overlapFraction = 1.0 - ((double) elapsedInWindow / windowSize);

            double effectiveCount = (entry.previousCount * overlapFraction)
                                  + entry.currentCount.get();

            if (effectiveCount >= config.getMaxRequests()) {
                return false;
            }

            entry.currentCount.incrementAndGet();
            return true;
        }
    }

    /** Internal holder for sliding window state per key. */
    private static class SlidingEntry {
        long currentWindowId = -1;
        AtomicInteger currentCount = new AtomicInteger(0);
        int previousCount = 0;
    }
}
