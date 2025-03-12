package com.example.bordados.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.bordados.model.OrderCustom;
import com.example.bordados.model.User;
import com.example.bordados.model.Enums.ShippingStatus;

@Repository
public interface OrderCustomRepository extends JpaRepository<OrderCustom, Long> {
    List<OrderCustom> findByUser(User user);

    // Método para buscar órdenes personalizadas que NO tienen un estado de envío específico
    @Query("SELECT o FROM OrderCustom o WHERE o.shippingStatus <> :status")
    List<OrderCustom> findByShippingStatusNot(@Param("status") ShippingStatus status);

    // Método para buscar órdenes personalizadas con un estado de envío específico
    List<OrderCustom> findByShippingStatus(ShippingStatus status);
}