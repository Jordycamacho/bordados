package com.example.bordados.controller.user;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.bordados.DTOs.CartDTO;
import com.example.bordados.DTOs.CustomizedOrderDetailDto;
import com.example.bordados.model.CustomizedOrderDetail;
import com.example.bordados.model.Order;
import com.example.bordados.model.OrderCustom;
import com.example.bordados.model.OrderDetail;
import com.example.bordados.model.PricingConfiguration;
import com.example.bordados.model.Product;
import com.example.bordados.model.User;
import com.example.bordados.repository.CustomizedOrderDetailRepository;
import com.example.bordados.repository.OrderCustomRepository;
import com.example.bordados.repository.OrderDetailRepository;
import com.example.bordados.repository.OrderRepository;
import com.example.bordados.service.CartService;
import com.example.bordados.service.IUserService;
import com.example.bordados.service.ProductService;
import com.example.bordados.service.ServiceImpl.OrderServiceImpl;
import com.example.bordados.service.ServiceImpl.PricingServiceImpl;
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
    private final PricingServiceImpl pricingService;
    private final OrderRepository orderRepository;
    private final OrderCustomRepository orderCustomRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CustomizedOrderDetailRepository customizedOrderDetailRepository;

    public OrderController(CartService cartService, IUserService userService, OrderServiceImpl orderService,
            ProductService productService, StripeService stripeService, PricingServiceImpl pricingService,
            OrderRepository orderRepository, OrderCustomRepository orderCustomRepository, OrderDetailRepository orderDetailRepository
            , CustomizedOrderDetailRepository customizedOrderDetailRepository) {
        this.productService = productService;
        this.customizedOrderDetailRepository = customizedOrderDetailRepository;
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.orderCustomRepository = orderCustomRepository;
        this.pricingService = pricingService;
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

        // Obtener la configuración de precios
        PricingConfiguration pricing = pricingService.getPricingConfiguration();

        // Calcular el costo adicional
        double additionalCost = calculateAdditionalCost(customOrderDetail, pricing);
        customOrderDetail.setAdditionalCost(additionalCost);

        // Crear la orden personalizada
        OrderCustom orderCustom = orderService.createOrderCustom(customOrderDetail);
        redirectAttributes.addFlashAttribute("success",
                "Orden personalizada creada exitosamente. Número de seguimiento: " + orderCustom.getTrackingNumber());
        return "redirect:/bordados";
    }

    private double calculateAdditionalCost(CustomizedOrderDetailDto dto, PricingConfiguration pricing) {
        double additionalCost = 0.0;

        // Costo por tamaño del primer bordado
        switch (dto.getEmbroideryType()) {
            case "Mediano":
                additionalCost += pricing.getMediumSizeFirstEmbroideryPrice();
                break;
            case "Grande":
                additionalCost += pricing.getLargeSizeFirstEmbroideryPrice();
                break;
            default:
                // Pequeño no tiene costo adicional
                break;
        }

        // Costo por segundo bordado (si aplica)
        if (dto.isHasSecondEmbroidery()) {
            additionalCost += pricing.getSecondDesignPrice();

            // Costo por tamaño del segundo bordado
            switch (dto.getSecondEmbroideryType()) {
                case "Mediano":
                    additionalCost += pricing.getMediumSizeSecondEmbroideryPrice();
                    break;
                case "Grande":
                    additionalCost += pricing.getLargeSizeSecondEmbroideryPrice();
                    break;
                default:
                    // Pequeño no tiene costo adicional
                    break;
            }
        }

        // Costo por bordado en manga (si aplica)
        if (dto.isHasSleeveEmbroidery()) {
            additionalCost += pricing.getSleevePrice();
        }

        return additionalCost;
    }

    @GetMapping("/tracking")
    public String viewOrderTracking() {
        return "user/userOrderTracking";
    }

    @GetMapping("/usuario")
    public String viewOrdersUser(Model model, Principal principal) {
        // Obtener el usuario actual usando el servicio
        User user = userService.findUserByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Obtener las órdenes normales y personalizadas del usuario
        List<Order> orders = orderRepository.findByUser(user);
        List<OrderCustom> customOrders = orderCustomRepository.findByUser(user);

        // Pasar los datos a la vista
        model.addAttribute("user", user);
        model.addAttribute("orders", orders);
        model.addAttribute("customOrders", customOrders);

        return "user/userOrders";
    }

    @PostMapping("/usuario/actualizar")
    public String updateUserAddress(@RequestParam String address, Principal principal,
            RedirectAttributes redirectAttributes) {
        // Obtener el usuario actual
        User user = userService.findUserByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Actualizar la dirección
        user.setAddress(address);
        userService.save(user);

        // Redirigir con un mensaje de éxito
        redirectAttributes.addFlashAttribute("success", "Dirección actualizada correctamente.");
        return "redirect:/bordados/orden/usuario";
    }

    @GetMapping("/detalle/normal/{id}")
    public String showNormalOrderDetails(@PathVariable Long id, Model model) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        // Obtener los detalles de la orden (OrderDetail)
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrder(order);

        // Pasar los datos a la vista
        model.addAttribute("order", order);
        model.addAttribute("orderDetails", orderDetails);

        return "/admin/order/orderDetail";
    }

    @GetMapping("/detalle/custom/{id}")
    public String showCustomOrderDetails(@PathVariable Long id, Model model) {
        OrderCustom orderCustom = orderCustomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden personalizada no encontrada"));

        // Obtener los detalles de la orden personalizada (CustomizedOrderDetail)
        List<CustomizedOrderDetail> customizedOrderDetails = customizedOrderDetailRepository
                .findByOrderCustom(orderCustom);

        // Pasar los datos a la vista
        model.addAttribute("orderCustom", orderCustom);
        model.addAttribute("customizedOrderDetails", customizedOrderDetails);

        return "/admin/order/customOrderDetail";
    }
}