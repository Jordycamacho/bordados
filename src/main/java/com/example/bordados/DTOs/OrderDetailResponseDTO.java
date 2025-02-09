package com.example.bordados.DTOs;

import lombok.Data;

@Data
public class OrderDetailResponseDTO {
    private Long id;
    private String image;
    private double price;
    private String name;
    private String description;
    private String color;
    private String size;
    private int quantity;
    private String observations;
    private String customEmbroideryPhoto;
    private String embroideryType;
    private String embroideryPlacement;
    private String additionalDetails; // JSON con segundo bordado y bordado de manga
    private Long orderId;
    private Long productId;
}
