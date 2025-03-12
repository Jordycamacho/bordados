package com.example.bordados.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.bordados.model.Order;
import com.example.bordados.model.User;
import com.example.bordados.model.Enums.ShippingStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);

    // Método para buscar órdenes normales que NO tienen un estado de envío específico
    @Query("SELECT o FROM Order o WHERE o.shippingStatus <> :status")
    List<Order> findByShippingStatusNot(@Param("status") ShippingStatus status);

    // Método para buscar órdenes normales con un estado de envío específico
    List<Order> findByShippingStatus(ShippingStatus status);
}

