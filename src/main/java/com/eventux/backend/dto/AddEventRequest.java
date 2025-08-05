package com.eventux.backend.dto;

import com.eventux.backend.model.City;
import com.eventux.backend.model.EventCategory;
import lombok.Data;

@Data
public class AddEventRequest {

    private String eventName;
    private String email; // User (host) email
    private String phone;

    private String date; // format: yyyy-MM-dd
    private int people;
    private EventCategory eventCategory;

    private String streetName;
    private String streetNumber;
    private String postCode;
    private City city;

    private String comments;

    private Boolean hasManager;
    private String managerName;
    private String managerEmail;
    private String managerPhone;
}
