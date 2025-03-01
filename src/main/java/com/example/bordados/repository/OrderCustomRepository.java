package com.example.bordados.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.bordados.model.OrderCustom;

@Repository
public interface OrderCustomRepository extends JpaRepository<OrderCustom, Long> {
    
}
