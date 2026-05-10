package com.example.ningjingspa.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.ningjingspa.entity.Product;

@Repository
public interface ProductDao  extends JpaRepository<Product, Integer> {

	// 1. 關鍵字搜尋產品標題 (例如搜尋：精油、按摩)
    @Query(value = "SELECT * FROM product WHERE title LIKE %?1%", nativeQuery = true)
    List<Product> findByTitle(String title);

    // 2. 根據價格區間搜尋 (例如：搜尋 1000 ~ 2000 元的課程)
    @Query(value = "SELECT * FROM product WHERE price BETWEEN ?1 AND ?2", nativeQuery = true)
    List<Product> findByPriceRange(int minPrice, int maxPrice);

    // 3. 根據服務時長搜尋 (例如：找 60 分鐘或 90 分鐘的課程)
    @Query(value = "SELECT * FROM product WHERE duration = ?1", nativeQuery = true)
    List<Product> findByDuration(int duration);

    // 4. 依照價格由低到高排序
    @Query(value = "SELECT * FROM product ORDER BY price ASC", nativeQuery = true)
    List<Product> findAllOrderByPriceAsc();
    
    @Query("SELECT p FROM Product p WHERE p.title LIKE %:keyword% OR p.description LIKE %:keyword%")
    List<Product> findByTitleContainingOrDescriptionContaining(@Param("keyword") String keyword);
    
    List<Product> findByIsVisibleTrue();  // 前台使用
}
