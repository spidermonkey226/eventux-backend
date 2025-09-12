package com.eventux.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "files")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Files {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer fileID;

    @Column(name = "file_name")   // map to lowercase column
    private String fileName;

    @Column(name = "file_size")
    private String fileSize;

    @Column(name = "file_type")
    private String fileType;

    @ManyToOne
    @JoinColumn(name = "UserID")
    private User user;

    @ManyToOne
    @JoinColumn(name = "EventID")
    private Event event;

    @Lob
    @JdbcTypeCode(SqlTypes.LONGVARBINARY)
    private byte[] content;
}