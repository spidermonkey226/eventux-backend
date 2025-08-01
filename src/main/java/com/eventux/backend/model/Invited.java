package com.eventux.backend.model;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "invited")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Invited {

    @EmbeddedId
    private InvitedId id;

    private String email;

    private String firstName;

}