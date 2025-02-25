package com.example.bordados.DTOs;

import org.springframework.web.multipart.MultipartFile;

import com.example.bordados.model.CustomizedProductDetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomizedProductDTO {

    private Long productId;
    private Integer quantity;
    private String size;
    private String color;

    private String embroideryType;
    private String firstEmbroideryPlacement;
    private MultipartFile firstEmbroideryFile;
    private String observationsFirstEmbroidery;

    private boolean hasSecondEmbroidery;
    private String secondEmbroideryPlacement;
    private MultipartFile secondEmbroideryFile;
    private String secondEmbroideryType;
    private String observationsSecondEmbroidery;

    private boolean hasSleeveEmbroidery;
    private String sleeveSide;
    private String sleeveDesign;
    private String sleeveThreadColor;
    private String observations;

    private double extraCost; 

    public CustomizedProductDTO(CustomizedProductDetails details, double extraCost) {
        this.productId = details.getProduct().getId();
        this.quantity = details.getQuantity();
        this.size = details.getSize();
        this.color = details.getColor();

        this.embroideryType = details.getEmbroideryType();
        this.firstEmbroideryPlacement = details.getFirstEmbroideryPlacement();
        this.firstEmbroideryFile = null;
        this.observationsFirstEmbroidery = details.getObservationsFirstEmbroidery();

        this.hasSecondEmbroidery = details.isHasSecondEmbroidery();
        this.secondEmbroideryPlacement = details.getSecondEmbroideryPlacement();
        this.secondEmbroideryFile = null;
        this.secondEmbroideryType = details.getSecondEmbroideryType();
        this.observationsSecondEmbroidery = details.getObservationsSecondEmbroidery();

        this.hasSleeveEmbroidery = details.isHasSleeveEmbroidery();
        this.sleeveSide = details.getSleeveSide();
        this.sleeveDesign = details.getSleeveDesign();
        this.sleeveThreadColor = details.getSleeveThreadColor();
        this.observations = details.getObservations();

        this.extraCost = extraCost;
    }
}
