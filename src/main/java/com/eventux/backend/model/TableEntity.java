package com.eventux.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "table")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableEntity {

    @Id
    private int table_number;

    private int chair_count;

}