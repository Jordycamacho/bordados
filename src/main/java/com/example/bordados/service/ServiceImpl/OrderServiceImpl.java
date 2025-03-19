package com.example.bordados.service.ServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.bordados.DTOs.CustomizedOrderDetailDto;
import com.example.bordados.model.Cart;
import com.example.bordados.model.CustomizedOrderDetail;
import com.example.bordados.model.Discount;
import com.example.bordados.model.Order;
import com.example.bordados.model.OrderCustom;
import com.example.bordados.model.OrderDetail;
import com.example.bordados.model.PricingConfiguration;
import com.example.bordados.model.Product;
import com.example.bordados.model.User;
import com.example.bordados.model.Enums.DiscountType;
import com.example.bordados.model.Enums.ShippingStatus;
import com.example.bordados.repository.CartRepository;
import com.example.bordados.repository.DiscountRepository;
import com.example.bordados.repository.OrderCustomRepository;
import com.example.bordados.repository.OrderDetailRepository;
import com.example.bordados.repository.OrderRepository;
import com.example.bordados.repository.ProductRepository;
import com.example.bordados.repository.UserRepository;
import com.example.bordados.service.IUserService;
import com.example.bordados.service.ImageService;
import com.stripe.exception.StripeException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserRepository userRepository;
    private final ImageService imageService;
    private final IUserService userService;
    private final OrderCustomRepository orderCustomRepository;
    private final ProductRepository productRepository;
    private final StripeService stripeService;
    private final PricingServiceImpl pricingService;
    private final DiscountRepository discountRepository;

    public Order createOrder(Long userId, String paymentIntentId, String discountCode) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario con ID " + userId + " no encontrado"));
        List<Cart> cartItems = cartRepository.findByUser(user);

        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("El carrito está vacío");
        }
        for (Cart cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product.getQuantity() < cartItem.getQuantity()) {
                throw new IllegalStateException("Stock insuficiente para: " + product.getName());
            }
        }

        long totalInCents = calculateTotal(cartItems, discountCode, user);

        Order order = new Order();
        order.setUser(user);
        order.setCreatedDate(LocalDateTime.now());
        order.setShippingStatus(ShippingStatus.PENDING);
        order.setTrackingNumber(UUID.randomUUID().toString());
        order.setPaymentIntentId(paymentIntentId);
        order.setTotal(totalInCents / 100.0);
        Order savedOrder = orderRepository.save(order);

        try {
            String clientSecret = stripeService.createPaymentIntent(totalInCents, "eur", savedOrder.getId());
            savedOrder.setClientSecret(clientSecret);
            orderRepository.save(savedOrder);
        } catch (StripeException e) {
            throw new RuntimeException("Error creando el pago con Stripe: " + e.getMessage(), e);
        }

        createOrderDetails(savedOrder, cartItems);

        // Actualizar el inventario
        List<Product> updatedProducts = new ArrayList<>();
        for (Cart cartItem : cartItems) {
            Product product = cartItem.getProduct();
            int requestedQuantity = cartItem.getQuantity();
            product.setQuantity(product.getQuantity() - requestedQuantity);
            updatedProducts.add(product);
        }
        productRepository.saveAll(updatedProducts);

        // Vaciar el carrito
        cartRepository.deleteAllInBatch(cartItems);

        return savedOrder;
    }

    private long calculateTotal(List<Cart> cartItems, String discountCode, User user) {
        BigDecimal total = cartItems.stream()
                .map(cart -> BigDecimal.valueOf(cart.getProduct().getPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (discountCode != null && !discountCode.isEmpty()) {
            Optional<User> referrerOpt = userRepository.findUserByAffiliateCode(discountCode);
            if (referrerOpt.isPresent()) {
                User referrer = referrerOpt.get();
                
                if (user.getReferrer() != null) {
                    throw new IllegalArgumentException("Ya tienes un referido");
                }
                
                if (orderRepository.countByUser(user) > 0) {
                    throw new IllegalArgumentException("Código válido solo para primera compra");
                }

                total = total.multiply(BigDecimal.valueOf(0.9));
                
                user.setReferrer(referrer);
                userRepository.save(user);
                
                generarCodigoAfiliacion(referrer);
            }else {
                Discount discount = discountRepository.findByCode(discountCode).orElseThrow(() -> new IllegalArgumentException("Código inválido"));
                validarDescuento(discount);

                BigDecimal porcentaje = BigDecimal.valueOf(discount.getDiscountPercentage() / 100);
                total = total.subtract(total.multiply(porcentaje));
                actualizarUsosDescuento(discount);
            }
        }

        return total.multiply(BigDecimal.valueOf(100)).longValue();
    }

    private void validarDescuento(Discount discount) {
        if (!discount.isValid()) {
            throw new IllegalArgumentException("Código expirado o sin usos disponibles");
        }
    }

    private void actualizarUsosDescuento(Discount discount) {
        discount.setCurrentUses(discount.getCurrentUses() + 1);
        discountRepository.save(discount);
    }

    private void generarCodigoAfiliacion(User referidor) {
        Discount nuevoCodigo = Discount.builder()
                .code(UUID.randomUUID().toString().substring(0, 8))
                .discountPercentage(5.0)
                .type(DiscountType.AFFILIATE)
                .user(referidor)
                .expirationDate(LocalDateTime.now().plusMonths(1))
                .maxUses(1)
                .currentUses(0)
                .build();
        discountRepository.save(nuevoCodigo);
    }

    private void createOrderDetails(Order order, List<Cart> cartItems) {
        for (Cart cart : cartItems) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setUser(order.getUser());
            orderDetail.setOrder(order);
            orderDetail.setProduct(cart.getProduct()); // Usamos el producto directamente
            orderDetail.setQuantity(cart.getQuantity());
            orderDetail.setSize(cart.getSize());
            orderDetail.setColor(cart.getColor());

            orderDetailRepository.save(orderDetail);
        }
    }

    @Transactional
    public OrderCustom createOrderCustom(CustomizedOrderDetailDto dto, String paymentIntentId) {
        User user = userService.getCurrentUser();

        Product product = dto.getProduct();
        int requestedQuantity = dto.getQuantity();
        int availableQuantity = product.getQuantity();

        if (availableQuantity < requestedQuantity) {
            throw new IllegalStateException("No hay suficiente cantidad para el producto: " + product.getName() +
                    ". Disponible: " + availableQuantity + ", solicitado: " + requestedQuantity);
        }
        // Obtener la configuración de precios
        PricingConfiguration pricing = pricingService.getPricingConfiguration();

        // Crear la orden personalizada
        OrderCustom orderCustom = new OrderCustom();
        orderCustom.setUser(user);
        orderCustom.setCreatedDate(LocalDateTime.now());
        orderCustom.setShippingStatus(ShippingStatus.PENDING);
        orderCustom.setTrackingNumber(UUID.randomUUID().toString());
        orderCustom.setPaymentIntentId(paymentIntentId);

        // Calcular el costo adicional
        double additionalCost = calculateAdditionalCost(dto, pricing);

        // Calcular el total
        double total = (dto.getProduct().getPrice() + additionalCost) * dto.getQuantity();
        orderCustom.setTotal(total);

        // Crear el detalle de la orden personalizada
        CustomizedOrderDetail detail = new CustomizedOrderDetail();
        detail.setUser(user);
        detail.setProduct(dto.getProduct());
        detail.setQuantity(dto.getQuantity());
        detail.setSize(dto.getSize());
        detail.setColor(dto.getColor());
        detail.setEmbroideryType(dto.getEmbroideryType());
        detail.setFirstEmbroideryPlacement(dto.getFirstEmbroideryPlacement());
        detail.setFirstEmbroideryFile(imageService.saveImage(dto.getFirstEmbroideryFile()));
        detail.setObservationsFirstEmbroidery(dto.getObservationsFirstEmbroidery());

        // Segundo bordado (opcional)
        if (dto.isHasSecondEmbroidery()) {
            detail.setHasSecondEmbroidery(true);
            detail.setSecondEmbroideryPlacement(dto.getSecondEmbroideryPlacement());
            detail.setSecondEmbroideryFile(imageService.saveImage(dto.getSecondEmbroideryFile()));
            detail.setSecondEmbroideryType(dto.getSecondEmbroideryType());
            detail.setObservationsSecondEmbroidery(dto.getObservationsSecondEmbroidery());
        }

        // Bordado en manga (opcional)
        if (dto.isHasSleeveEmbroidery()) {
            detail.setHasSleeveEmbroidery(true);
            detail.setSleeveSide(dto.getSleeveSide());
            detail.setSleeveDesign(dto.getSleeveDesign());
            detail.setSleeveThreadColor(dto.getSleeveThreadColor());
        }

        // Asignar el costo adicional
        detail.setAdditionalCost(additionalCost);

        // Relacionar el detalle con la orden
        orderCustom.addCustomizedOrderDetail(detail);

        // Guardar la orden personalizada
        orderCustom = orderCustomRepository.save(orderCustom);

        product.setQuantity(availableQuantity - requestedQuantity);
        productRepository.save(product);

        return orderCustom;
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
}
