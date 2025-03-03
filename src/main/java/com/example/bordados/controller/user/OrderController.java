package com.example.bordados.controller.user;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.bordados.DTOs.CartDTO;
import com.example.bordados.DTOs.CustomizedOrderDetailDto;
import com.example.bordados.model.Order;
import com.example.bordados.model.OrderCustom;
import com.example.bordados.model.Product;
import com.example.bordados.model.User;
import com.example.bordados.service.CartService;
import com.example.bordados.service.IUserService;
import com.example.bordados.service.ProductService;
import com.example.bordados.service.ServiceImpl.OrderServiceImpl;

@Controller
@RequestMapping("/bordados/orden")
public class OrderController {

    // private static final Logger log =
    // LoggerFactory.getLogger(ProductUserController.class);

    private final CartService cartService;
    private final IUserService userService;
    private final OrderServiceImpl orderService;
    private final ProductService productService;

    public OrderController(CartService cartService, IUserService userService, OrderServiceImpl orderService,
            ProductService productService) {
        this.productService = productService;
        this.cartService = cartService;
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping("")
    public String viewOrder(Model model) {
        User user = userService.getCurrentUser();
        List<CartDTO> cartItems = cartService.getCartByUserId(user.getId());
        double total = cartItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        double shippingCost = 5.00;
        double stripeFee = (total + shippingCost) * 0.029 + 0.30;
        double finalTotal = total + shippingCost + stripeFee;

        model.addAttribute("user", user);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", finalTotal);

        return "user/userOrder";
    }

    @PostMapping("/crear")
    public String createOrder(RedirectAttributes redirectAttributes) {
        User user = userService.getCurrentUser();

        try {
            Order order = orderService.createOrder(user.getId());
            redirectAttributes.addFlashAttribute("success",
                    "Orden creada exitosamente. Número de seguimiento: " + order.getTrackingNumber());
            return "redirect:/bordados";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", "No puedes crear una orden con el carrito vacío.");
            return "redirect:/bordados/orden";
        }
    }

    @PostMapping("/createCustomOrder")
    public String createOrderCustom(@ModelAttribute CustomizedOrderDetailDto customOrderDetail,
            RedirectAttributes redirectAttributes) {
        User user = userService.getCurrentUser();

        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para realizar una compra.");
            return "redirect:/login";
        }

        // Asignar el producto al DTO
        Product product = productService.getProductById(customOrderDetail.getProductId());
        customOrderDetail.setProduct(product);

        // Crear la orden personalizada
        OrderCustom orderCustom = orderService.createOrderCustom(customOrderDetail);
        redirectAttributes.addFlashAttribute("success",
                "Orden personalizada creada exitosamente. Número de seguimiento: " + orderCustom.getTrackingNumber());
        return "redirect:/bordados";
    }
}