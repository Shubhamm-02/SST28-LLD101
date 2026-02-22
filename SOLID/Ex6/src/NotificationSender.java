/**
 * Base class for sending notifications.
 * LSP Contract:
 * 1. Subtypes must fulfill the send request without silently changing the
 * message content.
 * 2. If a notification is incompatible with the channel (e.g., missing phone
 * for SMS),
 * the subtype must throw a RuntimeException with a descriptive message.
 * 3. Callers must be prepared to handle these channel-specific validation
 * errors.
 */
public abstract class NotificationSender {
    protected final AuditLog audit;

    protected NotificationSender(AuditLog audit) {
        this.audit = audit;
    }

    /**
     * Sends the notification.
     * 
     * @throws RuntimeException if the notification cannot be sent via this channel.
     */
    public abstract void send(Notification n);
}
