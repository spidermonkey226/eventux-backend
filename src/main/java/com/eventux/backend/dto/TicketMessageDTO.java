package com.eventux.backend.dto;


import com.eventux.backend.model.TicketMessage;
import lombok.Value;

import java.time.Instant;

@Value
public class TicketMessageDTO {
    Long id;
    String sender;
    String text;
    Instant createdAt;
    Long authorUserId;

    public static TicketMessageDTO from(TicketMessage m) {
        Long authorId = null;
        if ("USER".equalsIgnoreCase(m.getSender())
                && m.getTicket() != null
                && m.getTicket().getReporter() != null
                && m.getTicket().getReporter().getIdUser() != null) {
            authorId = m.getTicket().getReporter().getIdUser().longValue();
        }
        return new TicketMessageDTO(m.getId(), m.getSender(), m.getText(), m.getCreatedAt(), authorId);
    }
}
