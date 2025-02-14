package com.example.bordados.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.bordados.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByNameCategory(String nameCategory);

}
