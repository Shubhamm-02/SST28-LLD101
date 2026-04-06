/**
 * Demo driver showing:
 * 1. Requests that don't need external calls bypass rate limiting entirely.
 * 2. Requests needing external calls are rate-limited per customer.
 * 3. Switching algorithms is a one-line change — no business logic changes.
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {

        // --- Configuration: 5 requests per 60 seconds ---
        RateLimiterConfig config = new RateLimiterConfig(5, 60_000);

        // --- Swap algorithms here (one-line change) ---
        // RateLimiter limiter = new SlidingWindowCounter(config);
        RateLimiter limiter = new FixedWindowCounter(config);

        ExternalResource api = new PaidExternalAPI();
        ExternalResourceGateway gateway = new ExternalResourceGateway(api, limiter);
        InternalService service = new InternalService(gateway);

        System.out.println("=========================================");
        System.out.println(" Rate Limiter: " + limiter.getClass().getSimpleName());
        System.out.println(" Config: " + config.getMaxRequests() + " requests / "
                           + (config.getWindowSizeInMillis() / 1000) + "s");
        System.out.println("=========================================\n");

        // --- Scenario 1: Requests that DON'T need external calls ---
        System.out.println("--- Scenario 1: Internal-only requests (no rate limiting) ---");
        service.handleRequest("customer:T1", "fetch user profile");
        service.handleRequest("customer:T1", "list orders");

        // --- Scenario 2: Requests that DO need external calls ---
        System.out.println("\n--- Scenario 2: Requests needing EXTERNAL resource ---");
        for (int i = 1; i <= 7; i++) {
            System.out.println("\n>> Request #" + i);
            service.handleRequest("customer:T1", "external enrichment #" + i);
        }

        // --- Scenario 3: Different customer (separate quota) ---
        System.out.println("\n--- Scenario 3: Different customer (separate quota) ---");
        service.handleRequest("customer:T2", "external lookup");
        service.handleRequest("customer:T2", "external lookup");

        System.out.println("\n=========================================");
        System.out.println(" Done. customer:T1 was rate-limited after 5 external calls.");
        System.out.println(" customer:T2 had its own independent quota.");
        System.out.println("=========================================");
    }
}
