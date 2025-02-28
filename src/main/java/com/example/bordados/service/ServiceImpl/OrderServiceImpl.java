package com.example.bordados.service.ServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.bordados.model.Cart;
import com.example.bordados.model.Order;
import com.example.bordados.model.OrderDetail;
import com.example.bordados.model.User;
import com.example.bordados.model.Enums.ShippingStatus;
import com.example.bordados.repository.CartRepository;
import com.example.bordados.repository.OrderDetailRepository;
import com.example.bordados.repository.OrderRepository;
import com.example.bordados.repository.UserRepository;

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

    public Order createOrder(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        List<Cart> cartItems = cartRepository.findByUser(user);
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("El carrito está vacío");
        }

        // Crear y guardar la orden
        Order order = new Order();
        order.setUser(user);
        order.setCreatedDate(LocalDateTime.now());
        order.setShippingStatus(ShippingStatus.PENDING);
        order.setTrackingNumber(UUID.randomUUID().toString());
        order.setTotal(calculateTotal(cartItems));
        Order savedOrder = orderRepository.save(order);

        // Crear los detalles de la orden
        createOrderDetails(savedOrder, cartItems);

        // Vaciar el carrito después de completar la orden
        cartRepository.deleteAll(cartItems);

        return savedOrder;
    }

    private double calculateTotal(List<Cart> cartItems) {
        return cartItems.stream()
                .mapToDouble(cart -> cart.getProduct().getPrice() * cart.getQuantity())
                .sum();
    }

    private void createOrderDetails(Order order, List<Cart> cartItems) {
        for (Cart cart : cartItems) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setUser(order.getUser());
            orderDetail.setOrder(order);
            orderDetail.setProduct(cart.getProduct());  // Usamos el producto directamente
            orderDetail.setQuantity(cart.getQuantity());
            orderDetail.setSize(cart.getSize());
            orderDetail.setColor(cart.getColor());

            orderDetailRepository.save(orderDetail);
        }
    }
}

