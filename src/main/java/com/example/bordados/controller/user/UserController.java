package com.example.bordados.controller.user;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.bordados.DTOs.CategorySubCategoryDTO;
import com.example.bordados.DTOs.UserDTO;
import com.example.bordados.model.Product;
import com.example.bordados.service.CategoryService;
import com.example.bordados.service.EmailService;
import com.example.bordados.service.IUserService;
import com.example.bordados.service.ProductService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/bordados")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private IUserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private EmailService emailService;

    @ModelAttribute("categoriesWithSub")
    public List<CategorySubCategoryDTO> getCategoriesWithSubCategories() {
        return categoryService.getAllCategoriesWithSubCategories();
    }
    
    @GetMapping("")
    public String getProducts(Model model) {
        List<Product> customizableProducts = productService.getCustomizableProducts();
        List<Product> bestsellrs = productService.getTopSellingProducts();
        List<Product> randomProducts = productService.getRandomProducts();

        model.addAttribute("customizableProducts", customizableProducts);
        model.addAttribute("bestsellers", bestsellrs);
        model.addAttribute("randomProducts", randomProducts);
        
        return "user/index";
    }

    @GetMapping("/categoria/{id}")
    public String getProductsByCategoryOrSubCategory(@PathVariable Long id, Model model) {
        List<Product> products = productService.getProductBySubCategory(id);
        model.addAttribute("products", products);
        return "user/products";
    }
    
    @GetMapping("/login")
    public String getLogin(@RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {

        if (error != null) {
            model.addAttribute("error", "Correo electrónico o contraseña incorrectos.");
        }
        if (logout != null) {
            model.addAttribute("message", "Has cerrado sesión correctamente.");
        }
        return "user/SingIn";
    }

    @GetMapping("/registro")
    public String getRegistro(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "user/SingUp";
    }

    @GetMapping("/login-success")
    public String postLogin(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            Collection<? extends GrantedAuthority > authorities = authentication.getAuthorities();
            for (GrantedAuthority grantedAuthority : authorities) {
                if (grantedAuthority.getAuthority().equals("ROLE_ADMIN")) {
                    return "redirect:/admin";
                }else if (grantedAuthority.getAuthority().equals("ROLE_USER")) {
                    return "redirect:/bordados";
                }
            }
        }
        return "redirect:/bordados/login";
    }

    @PostMapping("/registro")
    public String postRegistro(@Valid @ModelAttribute("userDTO") UserDTO userDTO, BindingResult result, Model model) {

        if (result.hasErrors()) {
            logger.warn("Error en el formulario de registro: {}", result.getAllErrors());
            return "user/SingUp";
        }

        try {
            // Registrar al usuario
            userService.registerUser(userDTO);
            logger.info("Usuario registrado exitosamente: {}", userDTO.getEmail());

            // Enviar correo de bienvenida
            emailService.sendWelcomeEmail(userDTO.getEmail(), userDTO.getName());

            return "redirect:/bordados/login";
        } catch (IllegalArgumentException e) {
            logger.error("Error durante el registro: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "user/SingUp";
        } catch (Exception e) {
            logger.error("Error inesperado durante el registro: {}", e.getMessage());
            model.addAttribute("error", "Ocurrió un error inesperado. Inténtalo de nuevo.");
            return "user/SingUp";
        }
    }

    @GetMapping("/afiliacion")
    public String getviewAffiliation() {
        return "user/affiliate";
    }

    @GetMapping("/guia")
    public String getviewGuide() {
        return "user/SizeGuide";
    }

    @GetMapping("/politica-privacidad")
    public String getviewPrivacyPolicy() {
        return "user/privacyPolicy";
    }

    @GetMapping("/politica-envios-rembolsos")
    public String getMethodName() {
        return "user/refundPolicy";
    }
    
    @GetMapping("/terminos-servicio")
    public String getviewTermsOfService() {
        return "user/termsService";
    }
}
