package com.example.bordados.controller.admin;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.bordados.DTOs.CategoryDTO;
import com.example.bordados.DTOs.CategorySubCategoryDTO;
import com.example.bordados.service.CategoryService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/admin/categorias")
public class CategoryController {

    private final CategoryService categoryService;
    private static final Logger log = LoggerFactory.getLogger(CategoryController.class);

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @ModelAttribute("categoriesWithSub")
    public List<CategorySubCategoryDTO> getCategoriesWithSubCategories() {
        return categoryService.getAllCategoriesWithSubCategories();
    }

    @GetMapping
    public String showAllCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/category/showCategory";
    }

    @GetMapping("/crear")
    public String showCreateForm(Model model) {
        model.addAttribute("category", new CategoryDTO());
        return "admin/category/createCategory";
    }

    @PostMapping("/crear")
    public String createCategory(@Valid @ModelAttribute("category") CategoryDTO categoryDTO, BindingResult result) {
        if (result.hasErrors()) {
            log.warn("Errores en el formulario de creación: {}", result.getAllErrors());
            return "admin/category/createCategory";
        }
        try {
            categoryService.createCategory(categoryDTO);
            log.info("Categoría creada exitosamente: {}", categoryDTO.getNameCategory());
            return "redirect:/admin/categorias";
        } catch (Exception e) {
            log.error("Error al crear categoría", e);
            return "/error";
        }
    }

    @GetMapping("/editar/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            CategoryDTO categoryDTO = categoryService.getCategoryById(id);
            model.addAttribute("category", categoryDTO);
            return "admin/category/editCategory";
        } catch (Exception e) {
            log.error("Error al cargar el formulario de edición para ID {}", id, e);
            return "redirect:/admin/categorias";
        }
    }
    
    @PostMapping("/editar/{id}")
    public String updateCategory(@PathVariable Long id, @Valid @ModelAttribute("category") CategoryDTO categoryDTO, BindingResult result) {
        if (result.hasErrors()) {
            log.warn("Errores en el formulario de edición: {}", result.getAllErrors());
            return "admin/category/editCategory";
        }
        try {
            categoryService.updateCategory(id, categoryDTO);
            log.info("Categoría actualizada correctamente con ID: {}", id);
            return "redirect:/admin/categorias";
        } catch (Exception e) {
            log.error("Error al actualizar categoría con ID {}", id, e);
            return "/error";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            log.info("Categoría eliminada con éxito: ID {}", id);
        } catch (Exception e) {
            log.error("Error al eliminar categoría con ID {}", id, e);
        }
        return "redirect:/admin/categorias";
    }
}