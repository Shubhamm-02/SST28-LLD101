/**
 * Configuration for a rate limiter.
 * Holds the maximum number of requests allowed within a time window.
 */
public class RateLimiterConfig {

    private final int maxRequests;
    private final long windowSizeInMillis;

    /**
     * @param maxRequests        maximum requests allowed per window (e.g. 100)
     * @param windowSizeInMillis window duration in milliseconds (e.g. 60_000 for 1 minute)
     */
    public RateLimiterConfig(int maxRequests, long windowSizeInMillis) {
        this.maxRequests = maxRequests;
        this.windowSizeInMillis = windowSizeInMillis;
    }

    public int getMaxRequests() {
        return maxRequests;
    }

    public long getWindowSizeInMillis() {
        return windowSizeInMillis;
    }
}
