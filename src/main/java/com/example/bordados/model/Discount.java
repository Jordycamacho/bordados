package com.example.bordados.model;

import java.time.LocalDateTime;

import com.example.bordados.model.Enums.DiscountType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private Double discountPercentage;

    @Enumerated(EnumType.STRING)
    private DiscountType type; // SUBSCRIPTION, AFFILIATE

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // Usuario dueño del código (para afiliación)

    @Column(nullable = false)
    private LocalDateTime expirationDate;

    @Column(nullable = false)
    private Integer maxUses;

    @Column(nullable = false)
    private Integer currentUses = 0;

    public boolean isValid() {
        return currentUses < maxUses && LocalDateTime.now().isBefore(expirationDate);
    }
}


