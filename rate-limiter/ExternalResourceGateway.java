/**
 * Gateway that sits between internal services and the external resource.
 * Checks the rate limiter BEFORE forwarding the call.
 *
 * This is the single point where rate limiting is enforced — internal
 * services never call the external resource directly.
 */
public class ExternalResourceGateway {

    private final ExternalResource externalResource;
    private final RateLimiter rateLimiter;

    public ExternalResourceGateway(ExternalResource externalResource, RateLimiter rateLimiter) {
        this.externalResource = externalResource;
        this.rateLimiter = rateLimiter;
    }

    /**
     * Attempt to call the external resource on behalf of the given key.
     *
     * @param key     rate-limiting key (e.g. "customer:T1")
     * @param request the request payload to send to the external resource
     * @return the response from the external resource, or null if rate-limited
     */
    public String call(String key, String request) {
        if (!rateLimiter.allowRequest(key)) {
            System.out.println("  [RATE LIMITED] Request denied for key=" + key);
            return null;
        }

        return externalResource.call(request);
    }
}
