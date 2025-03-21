package com.example.bordados.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.bordados.model.CustomizedProductDetails;

@Repository
public interface CustomizedProductDetailsRepository extends JpaRepository<CustomizedProductDetails, Long> {
}
