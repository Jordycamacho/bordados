package com.example.bordados.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.bordados.service.ServiceImpl.UserServiceImpl;
import com.example.bordados.service.ServiceImpl.WishlistServiceImpl;

@Controller
@RequestMapping("/bordados/wishlist")
public class WishlistController {
    @Autowired
    private WishlistServiceImpl wishlistService;
    @Autowired
    private UserServiceImpl userService;

    @GetMapping("/add/{productId}")
    public String addToWishlist(@PathVariable Long productId) {
        wishlistService.addToWishlist(productId);
        return "redirect:/bordados/wishlist";
    }

    @GetMapping("/remove/{id}")
    public String removeFromWishlist(@PathVariable("id") Long productId) {
        Long userId = userService.getCurrentUser().getId();
        wishlistService.removeFromWishlist(userId, productId);
        return "redirect:/bordados/wishlist";
    }

    @GetMapping("")
    public String viewWishlist(Model model) {
        Long userId = userService.getCurrentUser().getId();
        model.addAttribute("products", wishlistService.getWishlistByUser(userId));
        return "user/wishlist";
    }
}