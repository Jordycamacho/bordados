package com.example.bordados.service.ServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.bordados.DTOs.CustomizedOrderDetailDto;
import com.example.bordados.model.Cart;
import com.example.bordados.model.CustomizedOrderDetail;
import com.example.bordados.model.Order;
import com.example.bordados.model.OrderCustom;
import com.example.bordados.model.OrderDetail;
import com.example.bordados.model.Product;
import com.example.bordados.model.User;
import com.example.bordados.model.Enums.ShippingStatus;
import com.example.bordados.repository.CartRepository;
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

    public Order createOrder(Long userId, String paymentIntentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario con ID " + userId + " no encontrado"));

        List<Cart> cartItems = cartRepository.findByUser(user);
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("El carrito está vacío");
        }

        for (Cart cartItem : cartItems) {
            Product product = cartItem.getProduct();
            int requestedQuantity = cartItem.getQuantity();
            int availableQuantity = product.getQuantity();

            if (availableQuantity < requestedQuantity) {
                throw new IllegalStateException("No hay suficiente cantidad para el producto: " + product.getName() +
                        ". Disponible: " + availableQuantity + ", solicitado: " + requestedQuantity);
            }
        }

        // Crear y guardar la orden
        Order order = new Order();
        order.setUser(user);
        order.setCreatedDate(LocalDateTime.now());
        order.setShippingStatus(ShippingStatus.PENDING);
        order.setTrackingNumber(UUID.randomUUID().toString());
        order.setPaymentIntentId(paymentIntentId);

        long totalInCents = calculateTotal(cartItems);
        order.setTotal(totalInCents / 100.0);

        Order savedOrder = orderRepository.save(order);

        try {
            String clientSecret = stripeService.createPaymentIntent(totalInCents, "usd", savedOrder.getId());
            savedOrder.setClientSecret(clientSecret);
            orderRepository.save(savedOrder);
        } catch (StripeException e) {
            throw new RuntimeException("Error creando el pago con Stripe: " + e.getMessage(), e);
        }

        // Crear los detalles de la orden
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

    private long calculateTotal(List<Cart> cartItems) {
        BigDecimal total = cartItems.stream()
                .map(cart -> BigDecimal.valueOf(cart.getProduct().getPrice())
                        .multiply(BigDecimal.valueOf(cart.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return total.multiply(BigDecimal.valueOf(100)).longValue();
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
    public OrderCustom createOrderCustom(CustomizedOrderDetailDto dto) {
        User user = userService.getCurrentUser();

        // Crear la orden personalizada
        OrderCustom orderCustom = new OrderCustom();
        orderCustom.setUser(user);
        orderCustom.setCreatedDate(LocalDateTime.now());
        orderCustom.setShippingStatus(ShippingStatus.PENDING);
        orderCustom.setTrackingNumber(UUID.randomUUID().toString());

        // Calcular el total
        double total = dto.getProduct().getPrice() * dto.getQuantity() + dto.getAdditionalCost();
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

        // Calcular el costo adicional
        double additionalCost = detail.calculateAdditionalCost();
        detail.setAdditionalCost(additionalCost);

        // Relacionar el detalle con la orden
        orderCustom.addCustomizedOrderDetail(detail);

        // Guardar la orden personalizada (esto también guardará el detalle debido a
        // CascadeType.ALL)
        orderCustom = orderCustomRepository.save(orderCustom);

        return orderCustom;
    }


}
