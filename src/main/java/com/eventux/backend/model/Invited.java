package com.eventux.backend.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "invited")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Invited {


    @EmbeddedId
    private InvitedId id;

    @Column(name = "firstName")
    private String firstName;

    @Column(name = "note")
    private String note;

    @ManyToOne
    @MapsId("eventId") // Link the field inside InvitedId
    @JoinColumn(name = "EventId", referencedColumnName = "EventID")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Event event;

    @Column(name = "token", length = 255, unique = true)
    private String token;

    @Column(name = "token_expires_at")
    private Instant tokenExpiresAt;

    @Column(name = "coming")
    private Boolean coming;

    public Invited(InvitedId id, String firstName, String note, Event event) {
        this.id = id;
        this.firstName = firstName;
        this.note = note;
        this.event = event;
        // token/tokenExpiresAt/coming remain null at creation
    }

}