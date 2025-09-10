package com.eventux.backend.dto;

import com.eventux.backend.model.Invited;

public record InvitedDTO(Integer eventId, String email, String firstName, String note, Boolean coming) {
    public static InvitedDTO from(Invited e) {
        return new InvitedDTO(
                e.getId().getEventId(),
                e.getId().getEmail(),
                e.getFirstName(),
                e.getNote(),
                e.getComing()
        );
    }
}
