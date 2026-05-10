package com.example.ningjingspa.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ningjingspa.entity.OilOrderItem;

public interface OilOrderItemDao extends JpaRepository<OilOrderItem, Integer> {
    List<OilOrderItem> findByOrderId(Integer orderId);
}
