package com.example.bordados.DTOs;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class OrderDTO {
    
    @NotBlank(message = "Tracking number is required")
    private String trackingNumber;

    @NotNull(message = "Created date is required")
    private LocalDateTime createdDate;

    @NotNull(message = "Total is required")
    private double total;

    @NotNull(message = "User ID is required")
    private Long userId;
}
