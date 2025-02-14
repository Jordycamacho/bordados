package com.example.bordados.service.ServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.bordados.DTOs.CategoryDTO;
import com.example.bordados.model.Category;
import com.example.bordados.repository.CategoryRepository;
import com.example.bordados.service.CategoryService;


@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private static final Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        log.info("Obteniendo todas las categorías");
        return categoryRepository.findAll().stream()
                .map(category -> new CategoryDTO(category.getIdCategory(), category.getNameCategory()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDTO getCategoryById(Long id) {
        log.info("Buscando categoría con ID: {}", id);
        return categoryRepository.findById(id)
                .map(category -> new CategoryDTO(category.getIdCategory(), category.getNameCategory()))
                .orElseThrow(() -> {
                    log.error("Categoría con ID {} no encontrada", id);
                    return new RuntimeException("Categoría no encontrada");
                });
    }

    @Override
    @Transactional
    public void createCategory(CategoryDTO categoryDTO) {
        log.info("Creando nueva categoría: {}", categoryDTO.getNameCategory());
        Category category = new Category();
        category.setNameCategory(categoryDTO.getNameCategory());
        categoryRepository.save(category);
        log.info("Categoría creada exitosamente");
    }

    @Override
    @Transactional
    public void updateCategory(Long id, CategoryDTO categoryDTO) {
        log.info("Actualizando categoría con ID: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Categoría con ID {} no encontrada", id);
                    return new RuntimeException("Categoría no encontrada");
                });
        category.setNameCategory(categoryDTO.getNameCategory());
        categoryRepository.save(category);
        log.info("Categoría actualizada exitosamente");
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        log.info("Eliminando categoría con ID: {}", id);
        if (!categoryRepository.existsById(id)) {
            log.error("Categoría con ID {} no encontrada", id);
            throw new RuntimeException("Categoría no encontrada");
        }
        categoryRepository.deleteById(id);
        log.info("Categoría eliminada exitosamente");
    }
}
