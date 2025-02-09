package com.example.bordados.model;

import com.example.bordados.model.Enums.Color;
import com.example.bordados.model.Enums.Size;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "La descripción es obligatoria")
    private String description;

    @NotBlank(message = "La imagen es obligatoria")
    private String image;

    @NotNull(message = "La cantidad es obligatoria")
    private int quantity;

    @NotNull(message = "El precio es obligatorio")
    private double price;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "La talla es obligatoria")
    private Size size; // S, M, L, XL

    @Enumerated(EnumType.STRING)
    @NotNull(message = "El color es obligatorio")
    private Color color; // WHITE, BLACK

    @ManyToOne
    @JoinColumn(name = "idSubcategory")
    private SubCategory subCategory; // Relación opcional con SubCategory

    @ManyToOne
    @JoinColumn(name = "idCategory")
    private Category category;
}
