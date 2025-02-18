package com.example.bordados.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.bordados.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByCategoryIdCategory(Long idCategory);

    List<Product> findBySubCategoryIdSubcategory(Long idSubcategory);

    List<Product> findByDiscountGreaterThan(double discount);
}