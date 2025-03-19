package com.example.bordados.controller.user;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.bordados.DTOs.CartDTO;
import com.example.bordados.DTOs.CustomizedOrderDetailDto;
import com.example.bordados.model.CustomizedOrderDetail;
import com.example.bordados.model.Discount;
import com.example.bordados.model.Order;
import com.example.bordados.model.OrderCustom;
import com.example.bordados.model.OrderDetail;
import com.example.bordados.model.PricingConfiguration;
import com.example.bordados.model.Product;
import com.example.bordados.model.User;
import com.example.bordados.model.Enums.DiscountType;
import com.example.bordados.repository.CustomizedOrderDetailRepository;
import com.example.bordados.repository.DiscountRepository;
import com.example.bordados.repository.OrderCustomRepository;
import com.example.bordados.repository.OrderDetailRepository;
import com.example.bordados.repository.OrderRepository;
import com.example.bordados.repository.UserRepository;
import com.example.bordados.service.CartService;
import com.example.bordados.service.EmailService;
import com.example.bordados.service.IUserService;
import com.example.bordados.service.ProductService;
import com.example.bordados.service.ServiceImpl.OrderServiceImpl;
import com.example.bordados.service.ServiceImpl.PricingServiceImpl;
import com.example.bordados.service.ServiceImpl.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

import jakarta.mail.MessagingException;

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
    private final DiscountRepository discountRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public OrderController(CartService cartService, IUserService userService, OrderServiceImpl orderService,
            ProductService productService, StripeService stripeService, PricingServiceImpl pricingService,
            OrderRepository orderRepository, OrderCustomRepository orderCustomRepository,
            EmailService emailService,
            OrderDetailRepository orderDetailRepository, DiscountRepository discountRepository,
            CustomizedOrderDetailRepository customizedOrderDetailRepository, UserRepository userRepository) {
        this.productService = productService;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.discountRepository = discountRepository;
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
            double finalTotal = total;

            long amountInCents = (long) (finalTotal * 100);

            model.addAttribute("user", user);
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("total", finalTotal);
            model.addAttribute("stripePublicKey", stripePublicKey);
            String clientSecret = stripeService.createPaymentIntent(amountInCents, "usd", null);
            model.addAttribute("clientSecret", clientSecret);

            return "user/userOrder";
        } catch (Exception e) {
            model.addAttribute("error",
                    "Ocurrió un error al cargar la orden:" + e.getMessage());
            return "user/userOrder";
        }
    }

    @PostMapping("/crear")
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String paymentIntentId = request.get("paymentIntentId");
            String discountCode = request.get("discountCode");
            User user = userService.getCurrentUser();
            Order order = orderService.createOrder(user.getId(), paymentIntentId, discountCode);

            emailService.sendCustomOrderConfirmationEmail(
                user.getEmail(),
                order.getTrackingNumber(),
                user,
                order.getTotal());


            response.put("success", true);
            response.put("message", "Orden creada exitosamente. Número de seguimiento: " + order.getTrackingNumber());
            response.put("redirectUrl", "/bordados");

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

    @PostMapping("/validardescuento")
    public ResponseEntity<?> validarDescuento(@RequestBody Map<String, String> request) {
        String codigo = request.get("code");

        try {
            User currentUser = userService.getCurrentUser();
            double discountPercentage = 0.0;
            Optional<User> referrerOpt = userRepository.findUserByAffiliateCode(codigo);
            Discount discount = null; // Variable para descuentos normales

            if (referrerOpt.isPresent()) {
                User referrer = referrerOpt.get(); // Usuario dueño del código de referido

                // Lógica de afiliación
                if (currentUser.getReferrer() != null) {
                    throw new IllegalArgumentException("Ya tienes un referido");
                }

                if (orderRepository.countByUser(currentUser) > 100) {
                    throw new IllegalArgumentException("Válido solo para primera compra");
                }

                discountPercentage = 10.0; // 10% de descuento por afiliación

                // Generar un cupón de descuento del 5% para el referidor
                Discount referralDiscount = Discount.builder()
                        .code(UUID.randomUUID().toString().substring(0, 8)) // Código aleatorio
                        .discountPercentage(5.0) // 5% de descuento
                        .type(DiscountType.AFFILIATE)
                        .user(referrer) // El referidor recibe el cupón
                        .expirationDate(LocalDateTime.now().plusMonths(1)) // Válido por 1 mes
                        .maxUses(1) // Solo se puede usar una vez
                        .currentUses(0)
                        .build();
                discountRepository.save(referralDiscount);

                // Enviar correo electrónico con el cupón de descuento al referidor
                try {
                    emailService.sendReferedEmail(referrer.getEmail(), referrer.getName(), referralDiscount);
                } catch (MessagingException e) {
                    // Manejar el error de envío de correo
                    System.err.println("Error enviando el correo: " + e.getMessage());
                }
            } else {
                // Lógica de descuento normal
                discount = discountRepository.findByCode(codigo)
                        .orElseThrow(() -> new IllegalArgumentException("Código no válido"));

                if (!discount.isValid()) {
                    throw new IllegalArgumentException("Código expirado o ya usado");
                }
                discountPercentage = discount.getDiscountPercentage();
            }

            // Calcular totales
            List<CartDTO> cartItems = cartService.getCartByUserId(currentUser.getId());

            double total = cartItems.stream()
                    .mapToDouble(item -> item.getPrice() * item.getQuantity())
                    .sum();

            double finalTotal = total;

            // Aplicar descuento (convertir porcentaje a decimal)
            double newTotal = finalTotal * (1 - (discountPercentage / 100.0));

            long amountInCents = (long) (newTotal * 100);
            String newClientSecret = stripeService.createPaymentIntent(amountInCents, "usd", null);

            Map<String, Object> response = new HashMap<>();
            response.put("newTotal", newTotal);
            response.put("newClientSecret", newClientSecret);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Collections.singletonMap("error", "Error interno: " + e.getMessage()));
        }
    }

    @PostMapping("/validardescuento-custom")
    public ResponseEntity<?> validarDescuentoCustom(@RequestBody Map<String, Object> request) {
        String codigo = (String) request.get("code");

        try {
            // Validar que el campo 'total' esté presente y sea un número
            if (!request.containsKey("total") || request.get("total") == null) {
                throw new IllegalArgumentException("El campo 'total' es requerido");
            }

            // Convertir el total a double
            double total = Double.parseDouble(request.get("total").toString());

            User currentUser = userService.getCurrentUser();
            double discountPercentage = 0.0;
            Optional<User> referrerOpt = userRepository.findUserByAffiliateCode(codigo);
            Discount discount = null;

            // Lógica de afiliación
            if (referrerOpt.isPresent()) {
                User referrer = referrerOpt.get();
                if (currentUser.getReferrer() != null) {
                    throw new IllegalArgumentException("Ya tienes un referido");
                }

                if (orderRepository.countByUser(currentUser) > 100) {
                    throw new IllegalArgumentException("Válido solo para primera compra");
                }

                discountPercentage = 10.0;

                Discount referralDiscount = Discount.builder()
                        .code(UUID.randomUUID().toString().substring(0, 8))
                        .discountPercentage(5.0)
                        .type(DiscountType.AFFILIATE)
                        .user(referrer)
                        .expirationDate(LocalDateTime.now().plusMonths(1))
                        .maxUses(1)
                        .currentUses(0)
                        .build();
                discountRepository.save(referralDiscount);

                try {
                    emailService.sendReferedEmail(referrer.getEmail(), referrer.getName(), referralDiscount);
                } catch (MessagingException e) {
                    // Manejar el error de envío de correo
                    System.err.println("Error enviando el correo: " + e.getMessage());
                }
            } else {
                // Lógica de descuento normal
                discount = discountRepository.findByCode(codigo)
                        .orElseThrow(() -> new IllegalArgumentException("Código no válido"));

                if (!discount.isValid()) {
                    throw new IllegalArgumentException("Código expirado o ya usado");
                }
                discountPercentage = discount.getDiscountPercentage();
            }

            // Aplicar descuento (convertir porcentaje a decimal)
            double newTotal = total * (1 - (discountPercentage / 100.0));

            // Crear un nuevo PaymentIntent con el nuevo total
            long amountInCents = (long) (newTotal * 100);

            String originalPaymentIntentId = (String) request.get("paymentIntentId");
            String newClientSecret;
            String newPaymentIntentId;

            if (originalPaymentIntentId != null && !originalPaymentIntentId.isEmpty()) {
                // Actualizar el PaymentIntent existente
                PaymentIntent paymentIntent = PaymentIntent.retrieve(originalPaymentIntentId);
                Map<String, Object> updateParams = new HashMap<>();
                updateParams.put("amount", amountInCents);
                paymentIntent.update(updateParams);
                newClientSecret = paymentIntent.getClientSecret();
                newPaymentIntentId = paymentIntent.getId(); // Devuelve el mismo ID
            } else {
                // Crear un nuevo PaymentIntent si no existe uno original
                PaymentIntent paymentIntent = PaymentIntent.create(Map.of(
                        "amount", amountInCents,
                        "currency", "usd"));
                newClientSecret = paymentIntent.getClientSecret();
                newPaymentIntentId = paymentIntent.getId(); // Devuelve el nuevo ID
            }

            // Devolver el nuevo total, el clientSecret y el paymentIntentId actualizado
            Map<String, Object> response = new HashMap<>();
            response.put("newTotal", newTotal);
            response.put("newClientSecret", newClientSecret);
            response.put("paymentIntentId", newPaymentIntentId); // Asegúrate de devolver esto

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Collections.singletonMap("error", "Error interno: " + e.getMessage()));
        }
    }

    @PostMapping("/createCustomOrder")
    public String createOrderCustom(@RequestParam("paymentIntentId") String paymentIntentId,
            @ModelAttribute CustomizedOrderDetailDto customOrderDetail,
            RedirectAttributes redirectAttributes) throws MessagingException {

        try {
            User user = userService.getCurrentUser();
            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para realizar una compra.");
                return "redirect:/login";
            }
            // Verificar estado del pago
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            if (!"succeeded".equals(paymentIntent.getStatus())) {
                redirectAttributes.addFlashAttribute("error", "El pago no fue exitoso");
                return "redirect:/personalizar/" + customOrderDetail.getProductId();
            }

            // Resto de tu lógica actual...
            // Asignar el producto al DTO
            Product product = productService.getProductById(customOrderDetail.getProductId());
            customOrderDetail.setProduct(product);

            // Obtener la configuración de precios
            PricingConfiguration pricing = pricingService.getPricingConfiguration();

            // Calcular el costo adicional
            double additionalCost = calculateAdditionalCost(customOrderDetail, pricing);
            customOrderDetail.setAdditionalCost(additionalCost);
            OrderCustom orderCustom = orderService.createOrderCustom(customOrderDetail, paymentIntentId);

            emailService.sendCustomOrderConfirmationEmail(
                    user.getEmail(),
                    orderCustom.getTrackingNumber(),
                    user,
                    orderCustom.getTotal());

            redirectAttributes.addFlashAttribute("success",
                    "Orden creada! Número de seguimiento: " + orderCustom.getTrackingNumber());

            return "redirect:/bordados";

        } catch (StripeException e) {
            redirectAttributes.addFlashAttribute("error", "Error verificando pago: " + e.getMessage());
            return "redirect:/personalizar/" + customOrderDetail.getProductId();
        }

        // Crear la orden personalizada

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

        return "/user/orderDetail";
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

        return "/user/customOrderDetail";
    }

    @PostMapping("/createpaymentintent")
    @ResponseBody
    public ResponseEntity<?> createDynamicPaymentIntent(@RequestBody Map<String, Object> request) {
        try {
            if (!request.containsKey("amount")) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Falta el campo 'amount'"));
            }
            long amount = Long.parseLong(request.get("amount").toString());
            String currency = request.get("currency").toString();
            String clientSecret = stripeService.createPaymentIntent(amount, currency, null);
            return ResponseEntity.ok(Collections.singletonMap("clientSecret", clientSecret));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}