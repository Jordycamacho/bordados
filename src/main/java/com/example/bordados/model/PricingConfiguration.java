package com.example.bordados.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PricingConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private double secondDesignPrice=10.0;
    @Column(nullable = false)
    private double sleevePrice = 3.0; 

    @Column(nullable = false)
    private double smallSizeFirstEmbroideryPrice=0.0;
    @Column(nullable = false)
    private double mediumSizeFirstEmbroideryPrice=3.0;
    @Column(nullable = false)
    private double largeSizeFirstEmbroideryPrice=7.0; 

    @Column(nullable = false)
    private double smallSizeSecondEmbroideryPrice=0.0; 
    @Column(nullable = false)
    private double mediumSizeSecondEmbroideryPrice=3.0;
    @Column(nullable = false)
    private double largeSizeSecondEmbroideryPrice=7.0; 
}