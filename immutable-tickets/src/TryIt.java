import com.example.tickets.IncidentTicket;
import com.example.tickets.TicketService;

import java.util.List;

/**
 * Demo showing immutability in action.
 */
public class TryIt {

    public static void main(String[] args) {
        TicketService ticketManager = new TicketService();

        // Create ticket
        IncidentTicket baseTicket = ticketManager.createTicket(
                "TCK-1001",
                "reporter@example.com",
                "Payment failing on checkout"
        );
        System.out.println("Created: " + baseTicket);

        // "Update" returns NEW ticket - original unchanged
        IncidentTicket delegated = ticketManager.assign(baseTicket, "agent@example.com");
        IncidentTicket upgraded = ticketManager.escalateToCritical(delegated);

        System.out.println("\nAfter updates:");
        System.out.println("  Original : " + baseTicket);
        System.out.println("  Assigned : " + delegated);
        System.out.println("  Escalated: " + upgraded);

        // Try external tag mutation - will fail
        List<String> labelsList = upgraded.getTags();
        try {
            labelsList.add("HACKED_FROM_OUTSIDE");
            System.out.println("\nBUG: Mutation succeeded!");
        } catch (UnsupportedOperationException e) {
            System.out.println("\nExternal mutation blocked!");
        }

        // No setters exist - won't compile:
        // baseTicket.setPriority("LOW");
    }
}