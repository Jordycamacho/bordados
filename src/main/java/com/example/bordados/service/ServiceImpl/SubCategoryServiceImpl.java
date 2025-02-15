package com.example.bordados.service.ServiceImpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.bordados.DTOs.SubCategoryDTO;
import com.example.bordados.model.Category;
import com.example.bordados.model.SubCategory;
import com.example.bordados.repository.CategoryRepository;
import com.example.bordados.repository.SubCategoryRepository;
import com.example.bordados.service.SubCategoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubCategoryServiceImpl implements SubCategoryService {

    private final SubCategoryRepository subCategoryRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public List<SubCategoryDTO> getAllSubCategories() {
        log.info("Obteniendo todas las subcategorías");
        try {
            return subCategoryRepository.findAll().stream()
                    .map(subCat -> new SubCategoryDTO(
                            subCat.getIdSubcategory(),
                            subCat.getNameSubcategory(),
                            subCat.getCategory().getIdCategory(),
                            subCat.getCategory().getNameCategory()))
                    .toList();
        } catch (Exception e) {
            log.error("Error al obtener las subcategorías", e);
            throw new RuntimeException("Error al obtener las subcategorías", e);
        }
    }

    @Override
    public SubCategoryDTO getSubCategoryById(Long id) {
        log.info("Obteniendo subcategoría por ID: {}", id);
        return subCategoryRepository.findById(id)
                .map(subCat -> new SubCategoryDTO(
                        subCat.getIdSubcategory(),
                        subCat.getNameSubcategory(),
                        subCat.getCategory().getIdCategory(),
                        subCat.getCategory().getNameCategory()))
                .orElseThrow(() -> new RuntimeException("Subcategoría no encontrada"));
    }

    @Override
    public SubCategoryDTO createSubCategory(SubCategoryDTO subCategoryDTO) {
        log.info("Creando una nueva subcategoría: {}", subCategoryDTO.getNameSubCategory());
        try {
            Category category = categoryRepository.findById(subCategoryDTO.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
            SubCategory subCategory = new SubCategory();
            subCategory.setNameSubcategory(subCategoryDTO.getNameSubCategory());
            subCategory.setCategory(category);
            SubCategory saved = subCategoryRepository.save(subCategory);
            return new SubCategoryDTO(saved.getIdSubcategory(), saved.getNameSubcategory(),
                    saved.getCategory().getIdCategory(), saved.getCategory().getNameCategory());
        } catch (Exception e) {
            log.error("Error al crear la subcategoría", e);
            throw new RuntimeException("Error al crear la subcategoría", e);
        }
    }

    @Override
    public SubCategoryDTO updateSubCategory(Long id, SubCategoryDTO subCategoryDTO) {
        log.info("Actualizando subcategoría con ID: {}", id);
        try {
            SubCategory subCategory = subCategoryRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Subcategoría no encontrada"));

            Category category = categoryRepository.findById(subCategoryDTO.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

            subCategory.setNameSubcategory(subCategoryDTO.getNameSubCategory());
            subCategory.setCategory(category);
            SubCategory updated = subCategoryRepository.save(subCategory);
            return new SubCategoryDTO(updated.getIdSubcategory(), updated.getNameSubcategory(),
                    updated.getCategory().getIdCategory(), updated.getCategory().getNameCategory());
        } catch (Exception e) {
            log.error("Error al actualizar la subcategoría", e);
            throw new RuntimeException("Error al actualizar la subcategoría", e);
        }
    }

    @Override
    public void deleteSubCategory(Long id) {
        log.warn("Eliminando subcategoría con ID: {}", id);
        try {
            subCategoryRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Error al eliminar la subcategoría", e);
            throw new RuntimeException("Error al eliminar la subcategoría", e);
        }
    }
}

