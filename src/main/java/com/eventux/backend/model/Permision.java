package com.eventux.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "permision")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Permision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permision_ID") // match DB column exactly
    private Integer id;

    @Column(name = "permision_name")
    private String permisionName;

    private String role;

    private String name;
}
