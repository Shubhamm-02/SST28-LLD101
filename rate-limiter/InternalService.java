/**
 * An internal service that handles client API requests.
 * It may or may not need to call the external resource depending on business logic.
 */
public class InternalService {

    private final ExternalResourceGateway gateway;

    public InternalService(ExternalResourceGateway gateway) {
        this.gateway = gateway;
    }

    /**
     * Handle a client request.
     *
     * @param customerId the customer making the request
     * @param requestData the request payload
     */
    public void handleRequest(String customerId, String requestData) {
        System.out.println("[Service] Handling request from " + customerId + ": " + requestData);

        // --- Business logic decides if external call is needed ---
        boolean needsExternalCall = requiresExternalResource(requestData);

        if (!needsExternalCall) {
            System.out.println("  [Service] No external call needed — serving from internal data");
            return;
        }

        // --- Rate limiter is checked only when external call is needed ---
        String response = gateway.call(customerId, requestData);

        if (response != null) {
            System.out.println("  [Service] Got external response: " + response);
        } else {
            System.out.println("  [Service] External call was rate-limited — returning fallback");
        }
    }

    /**
     * Simulated business logic: decides if the external resource is needed.
     * In this demo, requests containing "external" trigger the external call.
     */
    private boolean requiresExternalResource(String requestData) {
        return requestData.toLowerCase().contains("external");
    }
}
