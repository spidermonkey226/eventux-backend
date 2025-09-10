package com.eventux.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TicketUpdateRequest {
    private String ticketTitle;
    private String ticketContent;
    private String ticketStatus;
}
