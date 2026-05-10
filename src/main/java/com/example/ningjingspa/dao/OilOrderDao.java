package com.example.ningjingspa.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ningjingspa.entity.OilOrder;

public interface OilOrderDao extends JpaRepository<OilOrder, Integer> {
    List<OilOrder> findByUserIdOrderByOrderDateDesc(Integer userId);
    List<OilOrder> findAllByOrderByOrderDateDesc();
}