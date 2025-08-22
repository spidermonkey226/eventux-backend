package com.eventux.backend.dto;

import com.eventux.backend.model.Invited;

public record RsvpInvitedDTO(
        Integer eventId,
        String email,
        String firstName,
        String note,
        Boolean coming,
        EventMini event
) {
    public static RsvpInvitedDTO from(Invited e) {
        var ev = e.getEvent();
        return new RsvpInvitedDTO(
                e.getId().getEventId(),
                e.getId().getEmail(),
                e.getFirstName(),
                e.getNote(),
                e.getComing(),
                new EventMini(ev.getEventID(), ev.getEventName()) // adjust getter names if different
        );
    }
    public record EventMini(Integer eventId, String name) {}
}
