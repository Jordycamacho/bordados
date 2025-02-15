package com.example.bordados.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.bordados.DTOs.SubCategoryDTO;
import com.example.bordados.repository.CategoryRepository;
import com.example.bordados.service.SubCategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/admin/subcategorias")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Subcategorías", description = "Operaciones CRUD para Subcategorías")
public class SubCategoryController {

    private final SubCategoryService subCategoryService;
    private final CategoryRepository categoryRepository;

    @GetMapping
    @Operation(summary = "Mostrar todas las subcategorías")
    public String showAllSubCategories(Model model) {
        model.addAttribute("subcategories", subCategoryService.getAllSubCategories());
        return "/admin/subcategory/showSubCategory";
    }

    @GetMapping("/crear")
    @Operation(summary = "Formulario para crear una nueva subcategoría")
    public String showCreateForm(Model model) {
        model.addAttribute("subCategory", new SubCategoryDTO());
        model.addAttribute("categories", categoryRepository.findAll());
        return "/admin/subcategory/createSubCategory";
    }

    @PostMapping("/crear")
    @Operation(summary = "Crear una nueva subcategoría")
    public String createSubCategory(@ModelAttribute @Valid SubCategoryDTO subCategoryDTO, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            return "/admin/subcategory/formSubCategory";
        }
        subCategoryService.createSubCategory(subCategoryDTO);
        return "redirect:/admin/subcategorias";
    }

    @GetMapping("/editar/{id}")
    @Operation(summary = "Formulario para editar una subcategoría existente")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("subCategory", subCategoryService.getSubCategoryById(id));
        model.addAttribute("categories", categoryRepository.findAll());
        return "/admin/subCategory/createSubCategory";
    }

    @PostMapping("/editar/{id}")
    @Operation(summary = "Actualizar una subcategoría existente")
    public String updateSubCategory(@PathVariable Long id, @ModelAttribute @Valid SubCategoryDTO subCategoryDTO, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            return "/admin/subcategory/formSubCategory";
        }
        subCategoryService.updateSubCategory(id, subCategoryDTO);
        return "redirect:/admin/subcategorias";
    }

    @GetMapping("/eliminar/{id}")
    @Operation(summary = "Eliminar una subcategoría")
    public String deleteSubCategory(@PathVariable Long id) {
        subCategoryService.deleteSubCategory(id);
        return "redirect:/admin/subcategorias";
    }
}

