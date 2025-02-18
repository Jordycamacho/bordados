package com.example.bordados.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.bordados.model.Product;
import com.example.bordados.repository.ProductRepository;

@Service
public class ProductService {

    private static final String IMAGE_DIRECTORY = "images/";
    private static final String DEFAULT_IMAGE = "default.jpg";

    @Autowired
    private ProductRepository productRepository;

    public Product createProduct(Product product, MultipartFile imageFile) throws IOException {
        if (product.getCategory() == null && product.getSubCategory() == null) {
            throw new IllegalArgumentException("El producto debe estar relacionado con una categoría o subcategoría.");
        }
        if (product.getCategory() != null && product.getSubCategory() != null) {
            throw new IllegalArgumentException("El producto no puede estar relacionado con una categoría y una subcategoría al mismo tiempo.");
        }
        if (!imageFile.isEmpty()) {
            String imagePath = saveImage(imageFile);
            product.setImage(imagePath);
        } else {
            product.setImage(DEFAULT_IMAGE);
        }
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product productDetails, MultipartFile imageFile) throws IOException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setQuantity(productDetails.getQuantity());
        product.setPrice(productDetails.getPrice());
        product.setSize(productDetails.getSize());
        product.setColor(productDetails.getColor());
        product.setCategory(productDetails.getCategory());
        product.setSubCategory(productDetails.getSubCategory());

        if (!imageFile.isEmpty()) {
            String imagePath = saveImage(imageFile);
            product.setImage(imagePath);
        }

        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryIdCategory(categoryId);
    }

    public List<Product> getProductsBySubCategory(Long subCategoryId) {
        return productRepository.findBySubCategoryIdSubcategory(subCategoryId);
    }

    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    private String saveImage(MultipartFile imageFile) throws IOException {
        byte[] bytes = imageFile.getBytes();
        Path path = Paths.get(IMAGE_DIRECTORY + imageFile.getOriginalFilename());
        Files.write(path, bytes);
        return path.toString();
    }
}
