package com.example.bordados.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.bordados.model.CustomizedOrderDetail;

@Repository
public interface CustomizedOrderDetailRepository extends JpaRepository<CustomizedOrderDetail, Long> {
    
}
