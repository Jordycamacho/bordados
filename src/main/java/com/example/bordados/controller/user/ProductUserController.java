package com.example.bordados.controller.user;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.bordados.DTOs.CategorySubCategoryDTO;
import com.example.bordados.DTOs.CustomizedProductDTO;
import com.example.bordados.model.PricingConfiguration;
import com.example.bordados.model.Product;
import com.example.bordados.service.CategoryService;
import com.example.bordados.service.CustomizedProductDetailsService;
import com.example.bordados.service.ProductService;
import com.example.bordados.service.ServiceImpl.PricingServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/bordados/producto/")
public class ProductUserController {

    private static final Logger log = LoggerFactory.getLogger(ProductUserController.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CustomizedProductDetailsService customizationService;

    @Autowired
    private PricingServiceImpl pricingService;
    
    @ModelAttribute("categoriesWithSub")
    public List<CategorySubCategoryDTO> getCategoriesWithSubCategories() {
        return categoryService.getAllCategoriesWithSubCategories();
    }

    @GetMapping("/vista/{id}")
    @Operation(summary = "Ver producto", description = "Muestra la vista del producto con detalles básicos.")
    public String viewProduct(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "user/product";
    }

    @Operation(summary = "Ver detalles de un producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/personalizar/{id}")
    public String viewProductCustom(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        PricingConfiguration pricing = pricingService.getPricingConfiguration();
        model.addAttribute("product", product);
        model.addAttribute("pricing", pricing);
        return "user/productCustom";
    }

    @Operation(summary = "Guardar personalización de un producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Personalización guardada"),
            @ApiResponse(responseCode = "400", description = "Error en los datos")
    })
    @PostMapping("/save")
    public String saveCustomization(
            @ModelAttribute @Valid CustomizedProductDTO dto) {

        log.info("Recibiendo solicitud para personalizar producto con ID: {}", dto.getProductId());

        CustomizedProductDTO savedProduct = customizationService.saveCustomization(dto);

        log.info("Producto personalizado guardado con éxito. ID: {}", savedProduct.getProductId());

        return "redirect:/bordados/orden";
    }

    @GetMapping("/preview/{id}")
    public String previewEmbroidery(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);

        model.addAttribute("product", product);
        return "user/previewEmbroidery";
    }
}
