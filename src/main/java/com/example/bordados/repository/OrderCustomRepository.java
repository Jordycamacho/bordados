package com.example.bordados.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.bordados.model.OrderCustom;
import com.example.bordados.model.User;

@Repository
public interface OrderCustomRepository extends JpaRepository<OrderCustom, Long> {
    List<OrderCustom> findByUser(User user);
}
