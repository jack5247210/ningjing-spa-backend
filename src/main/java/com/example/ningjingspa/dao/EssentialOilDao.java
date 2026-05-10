package com.example.ningjingspa.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.ningjingspa.entity.EssentialOil;

public interface EssentialOilDao extends JpaRepository<EssentialOil, Integer> {
    
    // 根据分类查询
    List<EssentialOil> findByCategory(String category);

    // 获取所有不重复的分类
    @Query("SELECT DISTINCT e.category FROM EssentialOil e WHERE e.category IS NOT NULL AND e.category != '' ORDER BY e.category")
    List<String> findDistinctCategories();
}


