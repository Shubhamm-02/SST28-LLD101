public class PushNotificationSender extends NotificationSender {
    public PushNotificationSender(AuditLog audit) {
        super(audit);
    }

    @Override
    public void send(Notification n) {
        // Push notifications don't need email or phone in this demo,
        // but let's assume they need a body.
        if (n.body == null || n.body.isEmpty()) {
            throw new RuntimeException("Push notification body cannot be empty");
        }
        // System.out.println("PUSH -> body=" + n.body);
        audit.add("push sent");
    }
}
