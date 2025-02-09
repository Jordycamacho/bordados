package com.example.bordados.model;

import java.time.LocalDateTime;

import com.example.bordados.model.Enums.ShippingStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class ShippingTracking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String trackingNumber;

    @Enumerated(EnumType.STRING)
    private ShippingStatus status;

    private String location;
    private String notes;
    private LocalDateTime updateTime;

    @ManyToOne
    private Order order;

}

