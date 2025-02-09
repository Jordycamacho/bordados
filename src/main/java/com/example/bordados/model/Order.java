package com.example.bordados.model;

import java.time.LocalDateTime;

import com.example.bordados.model.Enums.ShippingStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
@Table(name = "order_customer")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tracking number is required")
    private String trackingNumber;

    @NotNull(message = "Created date is required")
    private LocalDateTime createdDate;

    @NotNull(message = "Total is required")
    private double total;

    @Enumerated(EnumType.STRING)
    private ShippingStatus shippingStatus; // PENDING, SHIPPED, IN_TRANSIT, DELIVERED, CANCELED

    private boolean productStatus;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
