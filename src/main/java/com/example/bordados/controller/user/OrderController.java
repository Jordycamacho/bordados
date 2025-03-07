package com.example.bordados.controller.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
import com.example.bordados.service.ServiceImpl.StripeService;

@Controller
@RequestMapping("/bordados/orden")
public class OrderController {

    // private static final Logger log =
    // LoggerFactory.getLogger(ProductUserController.class);

    @Value("${stripe.key.public}")
    private String stripePublicKey;

    private final CartService cartService;
    private final IUserService userService;
    private final OrderServiceImpl orderService;
    private final ProductService productService;
    private final StripeService stripeService;

    public OrderController(CartService cartService, IUserService userService, OrderServiceImpl orderService,
            ProductService productService, StripeService stripeService) {
        this.productService = productService;
        this.stripeService = stripeService;
        this.cartService = cartService;
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping("")
    public String viewOrder(Model model) {
        try {
            User user = userService.getCurrentUser();
            List<CartDTO> cartItems = cartService.getCartByUserId(user.getId());

            if (cartItems.isEmpty()) {
                model.addAttribute("error", "El carrito está vacío.");
                return "user/userOrder";
            }

            double total = cartItems.stream()
                    .mapToDouble(item -> item.getPrice() * item.getQuantity())
                    .sum();
            double shippingCost = 5.00;
            double stripeFee = (total + shippingCost) * 0.029 + 0.30;
            double finalTotal = total + shippingCost + stripeFee;

            long amountInCents = (long) (finalTotal * 100);

            model.addAttribute("user", user);
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("total", finalTotal);
            model.addAttribute("stripePublicKey", stripePublicKey);
            String clientSecret = stripeService.createPaymentIntent(amountInCents, "usd", null);
            model.addAttribute("clientSecret", clientSecret);

            System.out.println("Stripe Public Key:---------------------------------------- " + stripePublicKey);
            System.out.println("Client Secret: --------------------------------------------" + clientSecret);
            return "user/userOrder";
        } catch (Exception e) {
            model.addAttribute("error",
                    "Ocurrió un error al cargar la orden:------------------------------------ " + e.getMessage());
            return "user/userOrder";
        }
    }

    @PostMapping("/crear")
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String paymentIntentId = request.get("paymentIntentId");
            User user = userService.getCurrentUser();
            Order order = orderService.createOrder(user.getId(), paymentIntentId);

            response.put("success", true);
            response.put("message", "Orden creada exitosamente. Número de seguimiento: " + order.getTrackingNumber());
            response.put("redirectUrl", "/bordados"); // URL de redirección

            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            response.put("success", false);
            response.put("message", "No puedes crear una orden con el carrito vacío.");
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Ocurrió un error al crear la orden: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
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

    @GetMapping("/tracking")
    public String viewOrderTracking() {
        return "user/userOrderTracking";
    }
}