package com.example.bordados.service;

import java.util.List;

import com.example.bordados.DTOs.CategoryDTO;
import com.example.bordados.DTOs.CategorySubCategoryDTO;

public interface CategoryService {

    List<CategorySubCategoryDTO> getAllCategoriesWithSubCategories();
    List<CategoryDTO> getAllCategories();
    CategoryDTO getCategoryById(Long id);
    void createCategory(CategoryDTO categoryDTO);
    void updateCategory(Long id, CategoryDTO categoryDTO);
    void deleteCategory(Long id);
}
