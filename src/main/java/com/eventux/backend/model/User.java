package com.eventux.backend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.Lob;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idUser")
    private Integer idUser;

    @Column(name = "firstName")
    private String firstName;

    @Column(name = "lastName")
    private String lastName;

    @Column(name = "password")
    private String password;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;


    @ManyToOne
    @JoinColumn(name = "permision", referencedColumnName = "permision_ID")
    private Permision permision;
    @Lob
    @JsonIgnore
    @JdbcTypeCode(SqlTypes.LONGVARBINARY)          // 👈 force Hibernate to treat it as long varbinary
    @Column(name = "avatar", columnDefinition = "LONGBLOB") // 👈 match actual MySQL type
    private byte[] avatar;

    @Column(name = "avatar_content_type")
    private String avatarContentType;

    @Enumerated(EnumType.ORDINAL)
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    @Column(name = "subscription_level")
    private SubscriptionLevel subscriptionLevel = SubscriptionLevel.Free;

    @Column(name = "subscription_start")
    private LocalDate subscriptionStart;

    @Column(name = "subscription_end")
    private LocalDate subscriptionEnd;
}

