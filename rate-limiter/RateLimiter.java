/**
 * Core interface for rate limiting.
 * Any rate limiting algorithm implements this single method.
 * The key identifies WHO is being rate-limited (customer, tenant, API key, etc.).
 */
public interface RateLimiter {

    /**
     * Check whether a request identified by the given key is allowed.
     *
     * @param key the rate-limiting key (e.g. "customer:T1", "tenant:acme")
     * @return true if the request is allowed, false if it should be denied
     */
    boolean allowRequest(String key);
}
