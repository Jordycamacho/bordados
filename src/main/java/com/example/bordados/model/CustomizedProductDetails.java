package com.example.bordados.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomizedProductDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private Integer quantity; 
    @NotBlank
    private String size;
    @NotBlank
    private String color;

    @OneToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull
    private String embroideryType; // Bordado estándar o premium
    @NotNull
    private String firstEmbroideryPlacement; // Lado izquierdo, pecho, espalda
    @NotBlank
    private String firstEmbroideryFile; // Ruta del archivo subido
    private String ObservationsFirstEmbroidery;

    private boolean hasSecondEmbroidery; // segundo bordado
    private String secondEmbroideryPlacement;
    private String secondEmbroideryFile;
    private String secondEmbroideryType;
    private String observationsSecondEmbroidery;

    private boolean hasSleeveEmbroidery;
    private String sleeveSide; // Izquierda o derecha
    private String sleeveDesign;
    private String sleeveThreadColor;

    @Column(columnDefinition = "TEXT")
    private String observations;

    public double calculateAdditionalCost() {
        double extraCost = 0.0;
        if (hasSecondEmbroidery)
            extraCost += 23.0;
        if (hasSleeveEmbroidery)
            extraCost += 15.0;
        return extraCost;
    }
}
