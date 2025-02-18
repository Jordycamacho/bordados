package com.example.bordados.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.bordados.DTOs.ProductDTO;
import com.example.bordados.model.Product;

public interface ProductService {

    Product createProduct(ProductDTO productDTO, MultipartFile imageFile);

    Product updateProduct(Long id, ProductDTO productDetails, MultipartFile imageFile);

    Product getProductById(Long id);
    
    ProductDTO convertToDTO(Product product);  

    List<Product> getAllProduct();

    List<Product> getProducsByCategory(Long categoryId);

    List<Product> getProductBySubCategory(Long subCategoryId);

    List<Product> searchProductsByName(String name);

    List<Product> getDiscountedProducts();

    void deleteProduct(Long id);

    double calculateDiscountedPrice(Product product, boolean firstPurchase, boolean isAffiliate);
}
