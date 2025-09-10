package com.eventux.backend.dto;

import lombok.Data;

@Data
public class TicketCreateRequest {
    private String ticketTitle;
    private String ticketContent;
    private Long reporterId;     // optional
    private String reporterEmail; // optional (prefer email if present)
    private Long eventId;        // null => app ticket
}
