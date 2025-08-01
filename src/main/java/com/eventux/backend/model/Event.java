package com.eventux.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Table(name = "event")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EventID")
    private Integer eventID;


    private String EventName;

    @Enumerated(EnumType.STRING)
    @Column(name = "EventCatgory")
    private EventCategory eventCatgory;


    @ManyToOne
    @JoinColumn(name = "address")
    private Address address;

    @ManyToOne
    @JoinColumn(name = "User_manger_ID")
    private User manager;

    @ManyToOne
    @JoinColumn(name = "User_Host_ID")
    private User host;



    @ManyToOne
    @JoinColumn(name = "Files_fileID")
    private Files files;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Invited> inviteList;


}