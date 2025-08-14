package com.eventux.backend.dto;
import lombok.Data;
@Data
public class InviteCreateRequest {
    private Integer eventId;
    private String email;
    private String firstName;
    private String note; // optional
}