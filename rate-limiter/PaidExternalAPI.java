/**
 * Stub implementation of a paid external API.
 * Simulates a real external call that costs money per invocation.
 */
public class PaidExternalAPI implements ExternalResource {

    @Override
    public String call(String request) {
        System.out.println("  [ExternalAPI] Processing: " + request + " (you were charged $0.01)");
        return "response for " + request;
    }
}
