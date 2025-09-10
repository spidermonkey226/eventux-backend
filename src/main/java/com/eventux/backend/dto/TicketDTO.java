package com.eventux.backend.dto;

import com.eventux.backend.model.Ticket;
import lombok.Value;

@Value
public class TicketDTO {
    Long ticketId;
    String ticketTitle;
    String ticketStatus;
    String ticketContent;
    Long eventId;        // null for app tickets
    String reporterEmail;

    public static TicketDTO from(Ticket t) {
        Long evId = null;
        if (t.getEvent() != null && t.getEvent().getEventID() != null) {
            // getEventID() appears to be Integer -> convert to Long
            evId = t.getEvent().getEventID().longValue();
        }

        String reporterEmail = (t.getReporter() != null) ? t.getReporter().getEmail() : null;

        return new TicketDTO(
                t.getTicketId(),
                t.getTicketTitle(),
                t.getTicketStatus(),
                t.getTicketContent(),
                evId,
                reporterEmail
        );
    }
}
