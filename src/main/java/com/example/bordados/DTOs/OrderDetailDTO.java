package com.example.bordados.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class OrderDetailDTO {
    @NotBlank(message = "La imagen es obligatoria")
    private String image;

    @NotNull(message = "El precio es obligatorio")
    private double price;

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "La descripción es obligatoria")
    private String description;

    @NotBlank(message = "El color es obligatorio")
    private String color;

    @NotBlank(message = "La talla es obligatoria")
    private String size;

    @NotNull(message = "La cantidad es obligatoria")
    private int quantity;

    private String observations;

    @NotBlank(message = "La foto del bordado es obligatoria")
    private String customEmbroideryPhoto;

    @NotBlank(message = "El tipo de bordado es obligatorio")
    private String embroideryType;

    @NotBlank(message = "La ubicación del bordado es obligatoria")
    private String embroideryPlacement;

    private String additionalDetails; // JSON con segundo bordado y bordado de manga

    @NotNull(message = "El ID del pedido es obligatorio")
    private Long orderId;

    @NotNull(message = "El ID del producto es obligatorio")
    private Long productId;
}