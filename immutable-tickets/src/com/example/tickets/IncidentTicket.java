package com.example.tickets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Immutable incident ticket with Builder pattern.
 */
public final class IncidentTicket {

    private final String ticketCode;
    private final String submitterMail;
    private final String heading;
    private final String details;
    private final String severity;
    private final List<String> labels;
    private final String handlerMail;
    private final boolean clientAccessible;
    private final Integer responseTimeMinutes;
    private final String origin;

    // Private constructor - use Builder
    private IncidentTicket(Builder spec) {
        this.ticketCode = spec.ticketCode;
        this.submitterMail = spec.submitterMail;
        this.heading = spec.heading;
        this.details = spec.details;
        this.severity = spec.severity;
        // Defensive copy
        this.labels = spec.labels == null
                ? Collections.emptyList()
                : Collections.unmodifiableList(new ArrayList<>(spec.labels));
        this.handlerMail = spec.handlerMail;
        this.clientAccessible = spec.clientAccessible;
        this.responseTimeMinutes = spec.responseTimeMinutes;
        this.origin = spec.origin;
    }

    // Getters only - no setters
    public String getId() { return ticketCode; }
    public String getReporterEmail() { return submitterMail; }
    public String getTitle() { return heading; }
    public String getDescription() { return details; }
    public String getPriority() { return severity; }
    public List<String> getTags() { return labels; }
    public String getAssigneeEmail() { return handlerMail; }
    public boolean isCustomerVisible() { return clientAccessible; }
    public Integer getSlaMinutes() { return responseTimeMinutes; }
    public String getSource() { return origin; }

    // Create builder from existing ticket
    public Builder toBuilder() {
        return new Builder()
                .id(this.ticketCode)
                .reporterEmail(this.submitterMail)
                .title(this.heading)
                .description(this.details)
                .priority(this.severity)
                .tags(new ArrayList<>(this.labels))
                .assigneeEmail(this.handlerMail)
                .customerVisible(this.clientAccessible)
                .slaMinutes(this.responseTimeMinutes)
                .source(this.origin);
    }

    @Override
    public String toString() {
        return "IncidentTicket{" +
                "id='" + ticketCode + '\'' +
                ", reporter='" + submitterMail + '\'' +
                ", title='" + heading + '\'' +
                ", priority='" + severity + '\'' +
                ", tags=" + labels +
                ", assignee='" + handlerMail + '\'' +
                '}';
    }

    // Builder class
    public static class Builder {
        private String ticketCode;
        private String submitterMail;
        private String heading;
        private String details;
        private String severity;
        private List<String> labels = new ArrayList<>();
        private String handlerMail;
        private boolean clientAccessible;
        private Integer responseTimeMinutes;
        private String origin;

        public Builder id(String ticketCode) {
            this.ticketCode = ticketCode;
            return this;
        }

        public Builder reporterEmail(String submitterMail) {
            this.submitterMail = submitterMail;
            return this;
        }

        public Builder title(String heading) {
            this.heading = heading;
            return this;
        }

        public Builder description(String details) {
            this.details = details;
            return this;
        }

        public Builder priority(String severity) {
            this.severity = severity;
            return this;
        }

        public Builder tags(List<String> labels) {
            this.labels = labels == null ? new ArrayList<>() : new ArrayList<>(labels);
            return this;
        }

        public Builder addTag(String label) {
            this.labels.add(label);
            return this;
        }

        public Builder assigneeEmail(String handlerMail) {
            this.handlerMail = handlerMail;
            return this;
        }

        public Builder customerVisible(boolean clientAccessible) {
            this.clientAccessible = clientAccessible;
            return this;
        }

        public Builder slaMinutes(Integer responseTimeMinutes) {
            this.responseTimeMinutes = responseTimeMinutes;
            return this;
        }

        public Builder source(String origin) {
            this.origin = origin;
            return this;
        }

        // ALL validation happens here
        public IncidentTicket build() {
            // Required fields
            Validation.requireTicketId(ticketCode);
            Validation.requireEmail(submitterMail, "reporterEmail");
            Validation.requireNonBlank(heading, "title");
            Validation.requireMaxLen(heading, 80, "title");

            // Optional fields
            if (severity != null) {
                Validation.requireOneOf(severity, "priority", "LOW", "MEDIUM", "HIGH", "CRITICAL");
            }
            if (handlerMail != null) {
                Validation.requireEmail(handlerMail, "assigneeEmail");
            }
            if (responseTimeMinutes != null) {
                Validation.requireRange(responseTimeMinutes, 5, 7200, "slaMinutes");
            }

            return new IncidentTicket(this);
        }
    }
}