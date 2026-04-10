package com.shopflow.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenResponse {
    private String token;
    private String type;
    private String userId;
    private String email;
    private String role;
}