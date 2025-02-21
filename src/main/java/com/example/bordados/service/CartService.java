package com.example.bordados.service;

import java.util.List;

import com.example.bordados.DTOs.CartDTO;
import com.example.bordados.model.Enums.Color;
import com.example.bordados.model.Enums.Size;

public interface CartService {
    List<CartDTO> getCartByUserId(Long userId);

    void addProductToCart(Long userId, Long productId, int quantity, Size size, Color color);

    void removeFromCart(Long cartId);
}

