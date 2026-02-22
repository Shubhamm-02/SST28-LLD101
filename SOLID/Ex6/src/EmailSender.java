public class EmailSender extends NotificationSender {
    public EmailSender(AuditLog audit) {
        super(audit);
    }

    @Override
    public void send(Notification n) {
        // LSP fix: removed silent truncation. We deliver the full message.
        System.out.println("EMAIL -> to=" + n.email + " subject=" + n.subject + " body=" + n.body);
        audit.add("email sent");
    }
}
