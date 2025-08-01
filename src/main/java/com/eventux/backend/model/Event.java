package com.eventux.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "event")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long EventID;

    private String EventName;

    private String EventCatgory;

    @ManyToOne
    @JoinColumn(name = "address")
    private Address address;

    @ManyToOne
    @JoinColumn(name = "User_manger_ID")
    private User manager;

    @ManyToOne
    @JoinColumn(name = "User_Host_ID")
    private User host;

    private String inviteList;

    @ManyToOne
    @JoinColumn(name = "Files_fileID")
    private Files files;

}