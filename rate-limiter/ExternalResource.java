/**
 * Interface representing a paid external resource / API.
 * In a real system, this would be an HTTP client, SDK call, etc.
 */
public interface ExternalResource {

    String call(String request);
}
