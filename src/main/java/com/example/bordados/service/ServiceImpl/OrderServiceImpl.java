package com.example.bordados.service.ServiceImpl;

import java.time.LocalDateTime;
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

    public Order createOrder(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        List<Cart> cartItems = cartRepository.findByUser(user);
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("El carrito está vacío");
        }

        for (Cart cartItem : cartItems) {
            Product product = cartItem.getProduct();
            int requestedQuantity = cartItem.getQuantity();
            int availableQuantity = product.getQuantity(); // Usamos el campo "quantity"
    
            if (availableQuantity < requestedQuantity) {
                throw new IllegalStateException("No hay suficiente cantidad para el producto: " + product.getName());
            }
        }

        // Crear y guardar la orden
        Order order = new Order();
        order.setUser(user);
        order.setCreatedDate(LocalDateTime.now());
        order.setShippingStatus(ShippingStatus.PENDING);
        order.setTrackingNumber(UUID.randomUUID().toString());

        long totalInCents = calculateTotal(cartItems);
        order.setTotal(totalInCents / 100.0); 

        Order savedOrder = orderRepository.save(order);

        // Crear los detalles de la orden
        createOrderDetails(savedOrder, cartItems);

        for (Cart cartItem : cartItems) {
            Product product = cartItem.getProduct();
            int requestedQuantity = cartItem.getQuantity();
            product.setQuantity(product.getQuantity() - requestedQuantity); // Disminuir la cantidad
            productRepository.save(product); // Guardar el producto actualizado
        }

        // Vaciar el carrito después de completar la orden
        cartRepository.deleteAll(cartItems);

        return savedOrder;
    }

    private long calculateTotal(List<Cart> cartItems) {
        // Calcular el subtotal de los productos en el carrito
        double subtotal = cartItems.stream()
                .mapToDouble(cart -> cart.getProduct().getPrice() * cart.getQuantity())
                .sum();
    
        double shippingCost = 5.00;
    
        // Calcular la comisión de Stripe (2.9% + 0.30€)
        double stripeFee = (subtotal + shippingCost) * 0.029 + 0.30;
    
        // Calcular el total final
        double total = subtotal + shippingCost + stripeFee;
    
        // Convertir el total a céntimos
        return (long) (total * 100);
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
