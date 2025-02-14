package com.example.bordados.service.ServiceImpl;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.bordados.DTOs.CategoryDTO;
import com.example.bordados.model.Category;
import com.example.bordados.repository.CategoryRepository;
import com.example.bordados.service.CategoryService;
import com.example.bordados.service.FileStorageService;

@Service
public class CategoryServiceImpl implements CategoryService{

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private FileStorageService fileStorageService;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    
    @Override
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO, MultipartFile image) {
        
        Category category =  categoryRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Categoría no encontrada"));
        category.setNameCategory(categoryDTO.getNameCategory());

        if (image != null && !image.isEmpty()) {
            String imageUrl = fileStorageService.saveFile(image);
            category.setImgCategory(imageUrl);    
        }

        Category updatedCategory = categoryRepository.save(category);

        return new CategoryDTO(updatedCategory.getIdCategory(), updatedCategory.getNameCategory(), updatedCategory.getImgCategory());
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO, MultipartFile file) {
        
        LOGGER.info("Iniciando creación de categoría: {}", categoryDTO.getNameCategory());
        
        if (categoryRepository.findByNameCategory(categoryDTO.getNameCategory()).isPresent()) {
            throw new IllegalArgumentException("La categoría ya existe.");
        }

        String imageUrl = fileStorageService.saveFile(file);
        Category category = new Category();
        category.setNameCategory(categoryDTO.getNameCategory());
        category.setImgCategory(imageUrl);
        
        Category savedCategory = categoryRepository.save(category);
        LOGGER.info("Categoría creada con ID: {}", savedCategory.getIdCategory());

        return new CategoryDTO(savedCategory.getIdCategory(), savedCategory.getNameCategory(), savedCategory.getImgCategory());
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(cat -> new CategoryDTO(cat.getIdCategory(), cat.getNameCategory(), cat.getImgCategory()))
                .collect(Collectors.toList());

    }

    @Override
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Categoría no encontrada"));
        return new CategoryDTO(category.getIdCategory(), category.getNameCategory(), category.getImgCategory());
    }

    @Override
    public void deleteCategory(Long id) {
        
        LOGGER.info("Iniciando eliminación de categoría con ID: {}", id);
        if (categoryRepository.existsById(id)) {
            throw new NoSuchElementException("Categoría no encontrada");
        }
        categoryRepository.deleteById(id);
        LOGGER.info("Categoría eliminada con ID: {}", id);
    }
    
}
