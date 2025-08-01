package com.eventux.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "ticket")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long TicketId;

    private String TicketTitle;

    private String TicketStatus;

    private String TicketContent;

    @ManyToOne
    @JoinColumn(name = "reporterId")
    private User reporter;

    @ManyToOne
    @JoinColumn(name = "EventId")
    private Event event;

}