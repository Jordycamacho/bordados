package com.example.bordados.DTOs;

import org.springframework.web.multipart.MultipartFile;

import com.example.bordados.model.Product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomizedOrderDetailDto {
    private Long productId;
    private Product product;
    private Integer quantity;
    private String size;
    private String color;
    private String embroideryType; // Tamaño del primer bordado
    private String firstEmbroideryPlacement;
    private MultipartFile firstEmbroideryFile;
    private String observationsFirstEmbroidery;
    private boolean hasSecondEmbroidery;
    private String secondEmbroideryPlacement;
    private MultipartFile secondEmbroideryFile;
    private String secondEmbroideryType; // Tamaño del segundo bordado
    private String observationsSecondEmbroidery;
    private boolean hasSleeveEmbroidery;
    private String sleeveSide;
    private String sleeveDesign;
    private String sleeveThreadColor;
    private double additionalCost; // Costo adicional calculado
}

