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

    private String FileName;

    private String File_Size;

    private String file_type;

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