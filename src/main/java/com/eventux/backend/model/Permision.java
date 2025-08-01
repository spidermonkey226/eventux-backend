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
    private Long permision_ID;

    private String Permision_name;

    private String role;

}