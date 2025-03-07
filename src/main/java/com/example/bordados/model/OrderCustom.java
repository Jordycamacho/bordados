package com.example.bordados.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.bordados.model.Enums.ShippingStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
@Table(name = "order_custom")
public class OrderCustom {
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

    private String paymentIntentId; 

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "orderCustom", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CustomizedOrderDetail> customizedOrderDetails = new ArrayList<>();

    // Método para agregar detalles a la orden
    public void addCustomizedOrderDetail(CustomizedOrderDetail detail) {
        customizedOrderDetails.add(detail);
        detail.setOrderCustom(this);
    }
}