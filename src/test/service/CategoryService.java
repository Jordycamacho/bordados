package com.example.bordados.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.bordados.DTOs.CategoryDTO;

public interface CategoryService {

    CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO, MultipartFile image);
    
    CategoryDTO createCategory(CategoryDTO categoryDTO, MultipartFile file);
    
    List<CategoryDTO> getAllCategories();
    
    CategoryDTO getCategoryById(Long id);

    void deleteCategory(Long id);
}
