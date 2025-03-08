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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomizedOrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "order_custom_id", nullable = false)
    private OrderCustom orderCustom;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull
    private Integer quantity;

    @NotBlank
    private String size;

    @NotBlank
    private String color;

    @NotNull
    private String embroideryType;

    @NotNull
    private String firstEmbroideryPlacement;

    @NotBlank
    private String firstEmbroideryFile;

    private String observationsFirstEmbroidery;

    private boolean hasSecondEmbroidery;
    private String secondEmbroideryPlacement;
    private String secondEmbroideryFile;
    private String secondEmbroideryType;
    private String observationsSecondEmbroidery;

    private boolean hasSleeveEmbroidery;
    private String sleeveSide;
    private String sleeveDesign;
    private String sleeveThreadColor;

    @Column(columnDefinition = "TEXT")
    private String observations;

    private double additionalCost;

    public double calculateAdditionalCost() {
        double extraCost = 0.0;
        if (hasSecondEmbroidery) extraCost += 10.0;
        if (hasSleeveEmbroidery) extraCost += 3.0;
        return extraCost;
    }
}