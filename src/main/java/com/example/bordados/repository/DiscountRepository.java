package com.example.bordados.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.bordados.model.Discount;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
    
    // Buscar un descuento por código (el código es único)
    Optional<Discount> findByCode(String code);

    // Opcional: Verificar si un código existe
    boolean existsByCode(String code);
}
