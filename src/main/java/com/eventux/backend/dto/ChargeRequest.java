package com.eventux.backend.dto;

import lombok.AllArgsConstructor;

import lombok.Data;
@Data
public class ChargeRequest {
    private Integer planId;     // 0..4
    private String cardNumber;
    private String expiry;      // MM/YY
    private String cvv;
    private String name;
}

