package com.eventux.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import com.eventux.backend.model.User;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String message;
    private String token;
    private User user;
}
