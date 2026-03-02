package com.example.tickets;

/**
 * Service layer - all updates return NEW ticket instances.
 */
public class TicketService {

    public IncidentTicket createTicket(String ticketCode, String submitterMail, String heading) {
        return new IncidentTicket.Builder()
                .id(ticketCode)
                .reporterEmail(submitterMail)
                .title(heading)
                .priority("MEDIUM")
                .addTag("NEW")
                .build();
    }

    // Returns NEW ticket - original unchanged
    public IncidentTicket escalateToCritical(IncidentTicket currentTicket) {
        return currentTicket.toBuilder()
                .priority("CRITICAL")
                .addTag("ESCALATED")
                .build();
    }

    // Returns NEW ticket - original unchanged
    public IncidentTicket assign(IncidentTicket currentTicket, String handlerMail) {
        return currentTicket.toBuilder()
                .assigneeEmail(handlerMail)
                .build();
    }
}