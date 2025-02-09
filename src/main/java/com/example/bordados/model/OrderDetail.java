package com.example.bordados.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Campos del producto base
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

    // Campos para el bordado personalizado
    private String observations;

    @NotBlank(message = "La foto del bordado es obligatoria")
    private String customEmbroideryPhoto;

    @NotBlank(message = "El tipo de bordado es obligatorio")
    private String embroideryType;

    @NotBlank(message = "La ubicación del bordado es obligatoria")
    private String embroideryPlacement;

    // Campos opcionales en JSON
    @Column(columnDefinition = "JSON")
    private String additionalDetails;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}

