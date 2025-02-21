package com.example.bordados.service.ServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.bordados.DTOs.CartDTO;
import com.example.bordados.model.Cart;
import com.example.bordados.model.Product;
import com.example.bordados.model.User;
import com.example.bordados.model.Enums.Color;
import com.example.bordados.model.Enums.Size;
import com.example.bordados.repository.CartRepository;
import com.example.bordados.service.CartService;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductServiceImpl productService;

    @Autowired
    private UserServiceImpl userService;

    @Override
    public List<CartDTO> getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId).stream().map(cart -> 
            CartDTO.builder()
                .id(cart.getId())
                .productId(cart.getProduct().getId())
                .productName(cart.getProduct().getName())
                .quantity(cart.getQuantity())
                .size(cart.getSize())
                .color(cart.getColor())
                .price(cart.getProduct().getPrice())
                .image(cart.getProduct().getImage())
                .build()
        ).collect(Collectors.toList());
    }

    @Override
    public void addProductToCart(Long userId, Long productId, int quantity, Size size, Color color) {
        User user = userService.getUserById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Product product = productService.getProductById(productId);

        if (product.getQuantity() < quantity) {
            throw new IllegalArgumentException("Cantidad no disponible");
        }

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setProduct(product);
        cart.setQuantity(quantity);
        cart.setSize(size);
        cart.setColor(color);

        cartRepository.save(cart);
    }

    @Override
    public void removeFromCart(Long cartId) {
        cartRepository.deleteById(cartId);
    }
}

