package com.example.ningjingspa.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ningjingspa.entity.Consult;

@Repository
public interface ConsultDao extends JpaRepository<Consult, Integer>{

	// 如果你想根據使用者 ID 查詢他的所有諮詢紀錄，可以加這行：
    List<Consult> findByUserId(Integer userId);
    
    // 如果你想根據推薦的產品 ID 查詢紀錄：
    List<Consult> findByProductId(Integer productId);
}