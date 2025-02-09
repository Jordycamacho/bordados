package com.example.bordados.DTOs;

import lombok.Data;

import java.time.LocalDateTime;

import com.example.bordados.model.Enums.ShippingStatus;

@Data
public class OrderResponseDTO {
    private Long id;
    private String trackingNumber;
    private LocalDateTime createdDate;
    private double total;
    private ShippingStatus shippingStatus;
    private boolean productStatus;
    private Long userId;
}