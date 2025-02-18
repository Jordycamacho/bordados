package com.example.bordados.service.ServiceImpl;

import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.bordados.DTOs.ProductDTO;
import com.example.bordados.model.Product;
import com.example.bordados.repository.CategoryRepository;
import com.example.bordados.repository.ProductRepository;
import com.example.bordados.repository.SubCategoryRepository;
import com.example.bordados.service.ProductService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final ImageServiceImpl imageService;

    public ProductServiceImpl(ProductRepository productRepository, ImageServiceImpl imageService,
            CategoryRepository categoryRepository, SubCategoryRepository subCategoryRepository) {
        this.productRepository = productRepository;
        this.imageService = imageService;
        this.categoryRepository = categoryRepository;
        this.subCategoryRepository = subCategoryRepository;
    }

    @Override
    public Product createProduct(ProductDTO productDTO, MultipartFile imageFile) {
        try {
            Product product = convertToEntity(productDTO);
            String imageName = imageService.saveImage(imageFile);
            product.setImage(imageName);
            Product savedProduct = productRepository.save(product);
            log.info("Producto creado: {}", savedProduct.getId());
            return savedProduct;
        } catch (Exception e) {
            log.error("Error al crear producto: {}", e.getMessage());
            throw new RuntimeException("Error al crear producto", e);
        }
    }

    @Override
    public Product updateProduct(Long id, ProductDTO productDetails, MultipartFile imageFile) {
        try {
            Product existingProduct = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));

            // Eliminar imagen anterior si se sube una nueva
            if (imageFile != null && !imageFile.isEmpty()) {
                imageService.deleteImage(existingProduct.getImage());
                existingProduct.setImage(imageService.saveImage(imageFile));
            }

            existingProduct.setName(productDetails.getName());
            existingProduct.setDescription(productDetails.getDescription());
            existingProduct.setPrice(productDetails.getPrice());
            existingProduct.setQuantity(productDetails.getQuantity());
            existingProduct.setSizes(new HashSet<>(productDetails.getSizes()));
            existingProduct.setColors(new HashSet<>(productDetails.getColors()));
            existingProduct.setDiscount(productDetails.getDiscount());
            existingProduct.setCategory(categoryRepository.findById(productDetails.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada")));
            if (productDetails.getSubCategoryId() != null) {
                existingProduct.setSubCategory(subCategoryRepository.findById(productDetails.getSubCategoryId())
                        .orElseThrow(() -> new RuntimeException("Subcategoría no encontrada")));
            } else {
                existingProduct.setSubCategory(null);
            }

            Product updatedProduct = productRepository.save(existingProduct);
            log.info("Producto actualizado: {}", updatedProduct.getId());
            return updatedProduct;
        } catch (Exception e) {
            log.error("Error al actualizar producto: {}", e.getMessage());
            throw new RuntimeException("Error al actualizar producto", e);
        }
    }

    @Override
    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getProducsByCategory(Long categoryId) {
        return productRepository.findByCategoryIdCategory(categoryId);
    }

    @Override
    public List<Product> getProductBySubCategory(Long subCategoryId) {
        return productRepository.findBySubCategoryIdSubcategory(subCategoryId);
    }

    @Override
    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public List<Product> getDiscountedProducts() {
        return productRepository.findByDiscountGreaterThan(0.0);
    }

    @Override
    public void deleteProduct(Long id) {
        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
            imageService.deleteImage(product.getImage());
            productRepository.deleteById(id);
            log.info("Producto eliminado: {}", id);
        } catch (Exception e) {
            log.error("Error al eliminar producto: {}", e.getMessage());
            throw new RuntimeException("Error al eliminar producto", e);
        }
    }

    @Override
    public double calculateDiscountedPrice(Product product, boolean firstPurchase, boolean isAffiliate) {
        double price = product.getPrice();
        double discount = product.getDiscount();

        if (firstPurchase) {
            discount += 5.0;
        }
        if (isAffiliate) {
            discount += 5.0;
        }

        double finalPrice = price - ((discount / 100) * price);
        log.info("Precio calculado para producto {}: {} con {}% de descuento", product.getId(), finalPrice, discount);
        return finalPrice;
    }

    private Product convertToEntity(ProductDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setQuantity(dto.getQuantity());
        product.setPrice(dto.getPrice());
        product.setDiscount(dto.getDiscount());
        product.setSizes(dto.getSizes() != null ? new HashSet<>(dto.getSizes()) : new HashSet<>());
        product.setColors(dto.getColors() != null ? new HashSet<>(dto.getColors()) : new HashSet<>());
        product.setCategory(categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada")));
        if (dto.getSubCategoryId() != null) {
            product.setSubCategory(subCategoryRepository.findById(dto.getSubCategoryId())
                    .orElseThrow(() -> new RuntimeException("Subcategoría no encontrada")));
        }
        return product;
    }

    @Override
    public Product getProductById(Long id) {
        try {
            return productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        } catch (Exception e) {
            log.error("Error al obtener producto por ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public ProductDTO convertToDTO(Product product) {
        return ProductDTO.builder()
                .name(product.getName())
                .description(product.getDescription())
                .quantity(product.getQuantity())
                .price(product.getPrice())
                .discount(product.getDiscount())
                .sizes(new HashSet<>(product.getSizes()))
                .colors(new HashSet<>(product.getColors()))
                .categoryId(product.getCategory().getIdCategory())
                .subCategoryId(product.getSubCategory() != null ? product.getSubCategory().getIdSubcategory() : null)
                .image(product.getImage())
                .build();
    }

}
