package com.example.bordados.service.ServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.bordados.DTOs.ProductDTO;
import com.example.bordados.model.Product;
import com.example.bordados.model.User;
import com.example.bordados.model.Wishlist;
import com.example.bordados.repository.ProductRepository;
import com.example.bordados.repository.UserRepository;
import com.example.bordados.repository.WishlistRepository;
import com.example.bordados.service.WishlistService;

@Service
public class WishlistServiceImpl implements WishlistService {
    @Autowired
    private WishlistRepository wishlistRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserServiceImpl userService;

    @Override
    public void addToWishlist(Long productId) {
        User user = userService.getCurrentUser();
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setProduct(product);

        wishlistRepository.save(wishlist);
    }

    @Override
    public void removeFromWishlist(Long userId, Long productId) {
        User user = userRepository.findById(userId).orElseThrow();
        Product product = productRepository.findById(productId).orElseThrow();
        wishlistRepository.findByUserAndProduct(user, product)
                .ifPresent(wishlistRepository::delete);
    }

    @Override
    public List<ProductDTO> getWishlistByUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return wishlistRepository.findByUser(user).stream()
                .map(w -> ProductDTO.builder()
                        .id(w.getProduct().getId())
                        .name(w.getProduct().getName())
                        .description(w.getProduct().getDescription())
                        .image(w.getProduct().getImage())
                        .quantity(w.getProduct().getQuantity())
                        .price(w.getProduct().getPrice())
                        .discount(w.getProduct().getDiscount())
                        .sizes(w.getProduct().getSizes())
                        .colors(w.getProduct().getColors())
                        .categoryId(w.getProduct().getCategory().getIdCategory())
                        .subCategoryId(w.getProduct().getSubCategory() != null ? w.getProduct().getSubCategory().getIdSubcategory() : null)
                        .build())
                .collect(Collectors.toList());
    }
}

