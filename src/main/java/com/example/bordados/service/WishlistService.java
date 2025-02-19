package com.example.bordados.service;

import java.util.List;

import com.example.bordados.DTOs.ProductDTO;

public interface WishlistService {
    void addToWishlist(Long productId);
    void removeFromWishlist(Long userId, Long productId);
    List<ProductDTO> getWishlistByUser(Long userId);
}