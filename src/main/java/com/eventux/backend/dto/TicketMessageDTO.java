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

    public static TicketMessageDTO from(TicketMessage m) {
        return new TicketMessageDTO(m.getId(), m.getSender(), m.getText(), m.getCreatedAt());
    }
}
