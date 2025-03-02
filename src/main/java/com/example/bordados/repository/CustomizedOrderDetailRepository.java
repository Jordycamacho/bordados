package com.example.bordados.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.bordados.model.CustomizedOrderDetail;
import com.example.bordados.model.OrderCustom;

@Repository
public interface CustomizedOrderDetailRepository extends JpaRepository<CustomizedOrderDetail, Long> {
    List<CustomizedOrderDetail> findByOrderCustom(OrderCustom orderCustom);
}
