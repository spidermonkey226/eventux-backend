package com.eventux.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class InvitedId implements Serializable {

    @Column(name = "EventId")
    private Integer eventId;

    @Column(name = "email")
    private String email;

    public InvitedId() {}

    public InvitedId(Integer eventId, String email) {
        this.eventId = eventId;
        this.email = email;
    }

    // Getters and Setters
    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // equals and hashCode are required for composite keys
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InvitedId)) return false;
        InvitedId that = (InvitedId) o;
        return Objects.equals(eventId, that.eventId) &&
                Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, email);
    }
}
