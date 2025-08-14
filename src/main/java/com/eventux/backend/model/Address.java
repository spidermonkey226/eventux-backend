package com.eventux.backend.model;

import com.eventux.backend.model.City;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private int addressId;


    @Enumerated(EnumType.STRING)
    @Column(name = "City",nullable = false)
    private City city;


    @Column(name = "Street_name",nullable = false)
    private String streetName;

    @Column(name = "Street_number",nullable = false)
    private String streetNumber;

    @Column(name = "Post_Code",nullable = false)
    private String postCode;
}
