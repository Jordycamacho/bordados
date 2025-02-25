package com.example.bordados.controller.user;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.bordados.DTOs.CartDTO;
import com.example.bordados.model.User;
import com.example.bordados.service.CartService;
import com.example.bordados.service.IUserService;

@Controller
@RequestMapping("/bordados/orden")
public class OrderController {
    
    //private static final Logger log = LoggerFactory.getLogger(ProductUserController.class);

    private final CartService cartService;
    private final IUserService userService;

    public OrderController(CartService cartService, IUserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping("")
    public String viewOrder(Model model) {
        User user = userService.getCurrentUser();
        List<CartDTO> cartItems = cartService.getCartByUserId(user.getId());
        double total = cartItems.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
        
        model.addAttribute("user", user);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total);
        
        return "user/userOrder";
    }
}
