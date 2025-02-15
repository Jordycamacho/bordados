package com.example.bordados.service;

import java.util.List;

import com.example.bordados.DTOs.SubCategoryDTO;

public interface SubCategoryService {
    
    List<SubCategoryDTO> getAllSubCategories();

    SubCategoryDTO getSubCategoryById(Long id);

    SubCategoryDTO createSubCategory(SubCategoryDTO subCategoryDTO);

    SubCategoryDTO updateSubCategory(Long id, SubCategoryDTO subCategoryDTO);

    void deleteSubCategory(Long id);

}
