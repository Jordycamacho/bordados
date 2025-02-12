package com.example.bordados.DTOs;

import com.example.bordados.model.UserType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String address;
    private UserType type;
    private String affiliateCode;
}