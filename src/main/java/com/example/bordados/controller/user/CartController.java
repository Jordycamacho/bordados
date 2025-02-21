package com.example.bordados.controller.user;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.example.bordados.DTOs.CartDTO;
import com.example.bordados.model.User;
import com.example.bordados.model.Enums.Color;
import com.example.bordados.model.Enums.Size;
import com.example.bordados.service.CartService;
import com.example.bordados.service.IUserService;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequestMapping("/bordados/carrito")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private IUserService userService;

    @GetMapping("")
    public String ShowViewCart(Model model, Principal principal) {
        User user = userService.getCurrentUser();

        List<CartDTO> cartItems = cartService.getCartByUserId(user.getId());
        model.addAttribute("cartItems", cartItems);
        return "user/cart";
    }

    @PostMapping("/agregar")
    public String addToCart(@RequestParam Long productId,
            @RequestParam int quantity,
            @RequestParam String size,
            @RequestParam String color) {

        User user = userService.getCurrentUser(); // Obtiene el usuario autenticado

        try {
            Size selectedSize = Size.valueOf(size);
            Color selectedColor = Color.valueOf(color);

            cartService.addProductToCart(user.getId(), productId, quantity, selectedSize, selectedColor);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Talla o color inválido" + e);
        }

        return "redirect:/bordados/carrito";
    }

    @PostMapping("/eliminar")
    public String removeFromCart(@RequestParam("cartId") Long cartId, Principal principal) {
        
        cartService.removeFromCart(cartId);

        return "redirect:/bordados/carrito";
    }

}
