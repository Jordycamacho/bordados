package com.example.bordados.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.bordados.DTOs.CategorySubCategoryDTO;
import com.example.bordados.DTOs.SubCategoryDTO;
import com.example.bordados.repository.CategoryRepository;
import com.example.bordados.service.CategoryService;
import com.example.bordados.service.SubCategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/admin/subcategorias")
@RequiredArgsConstructor
@Slf4j
public class SubCategoryController {

    private final SubCategoryService subCategoryService;
    private final CategoryRepository categoryRepository;
    @Autowired
    private CategoryService categoryService;

    @ModelAttribute("categoriesWithSub")
    public List<CategorySubCategoryDTO> getCategoriesWithSubCategories() {
        return categoryService.getAllCategoriesWithSubCategories();
    }
    
    @GetMapping
    public String showAllSubCategories(Model model) {
        model.addAttribute("subcategories", subCategoryService.getAllSubCategories());
        return "admin/subcategory/showSubCategory";
    }

    @GetMapping("/crear")
    public String showCreateForm(Model model) {
        model.addAttribute("subCategory", new SubCategoryDTO());
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/subcategory/createSubCategory";
    }

    @PostMapping("/crear")
    public String createSubCategory(@ModelAttribute @Valid SubCategoryDTO subCategoryDTO, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            return "admin/subcategory/formSubCategory";
        }
        subCategoryService.createSubCategory(subCategoryDTO);
        return "redirect:/admin/subcategorias";
    }

    @GetMapping("/editar/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("subCategory", subCategoryService.getSubCategoryById(id));
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/subcategory/createSubCategory";
    }

    @PostMapping("/editar/{id}")
    public String updateSubCategory(@PathVariable Long id, @ModelAttribute @Valid SubCategoryDTO subCategoryDTO, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            return "admin/subcategory/formSubCategory";
        }
        subCategoryService.updateSubCategory(id, subCategoryDTO);
        return "redirect:/admin/subcategorias";
    }

    @GetMapping("/eliminar/{id}")
    public String deleteSubCategory(@PathVariable Long id) {
        subCategoryService.deleteSubCategory(id);
        return "redirect:/admin/subcategorias";
    }
}

