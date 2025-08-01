package com.eventux.backend.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;


@Embeddable
public class InvitedId implements Serializable {

    private Long EventId;
    private String email;

    public InvitedId() {}

    public InvitedId(Long EventId, String email) {
        this.EventId = EventId;
        this.email = email;
    }

    // Getters and Setters
    public Long getEventId() {
        return EventId;
    }

    public void setEventId(Long eventId) {
        EventId = eventId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InvitedId)) return false;
        InvitedId that = (InvitedId) o;
        return Objects.equals(EventId, that.EventId) &&
                Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(EventId, email);
    }
}
