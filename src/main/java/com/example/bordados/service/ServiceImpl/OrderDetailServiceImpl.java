package com.example.bordados.service.ServiceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.bordados.model.CustomizedProductDetails;
import com.example.bordados.model.Order;
import com.example.bordados.model.OrderDetail;
import com.example.bordados.repository.CustomizedProductDetailsRepository;
import com.example.bordados.repository.OrderDetailRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderDetailServiceImpl {

    private final OrderDetailRepository orderDetailRepository;
    private final CustomizedProductDetailsRepository customizedProductDetailsRepository;

    public List<OrderDetail> getOrderDetailsByOrder(Long orderId) {
        Order order = orderDetailRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Orden no encontrada"))
                .getOrder();
        return orderDetailRepository.findByOrder(order);
    }

    public Optional<OrderDetail> getOrderDetailById(Long orderDetailId) {
        return orderDetailRepository.findById(orderDetailId);
    }

    public OrderDetail createCustomizedOrderDetail(Order order, CustomizedProductDetails customizedDetails) {
        customizedProductDetailsRepository.save(customizedDetails);

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrder(order);
        orderDetail.setUser(order.getUser());

        return orderDetailRepository.save(orderDetail);
    }
}
