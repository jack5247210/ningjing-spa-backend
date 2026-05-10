package com.example.ningjingspa.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ningjingspa.entity.EssentialOil;
import com.example.ningjingspa.entity.OilOrder;
import com.example.ningjingspa.req.OilOrderReq;
import com.example.ningjingspa.res.OilOrderRes;
import com.example.ningjingspa.service.OilShopService;

@RestController
@RequestMapping("/api/oil-shop")
public class OilShopController {

    @Autowired private OilShopService oilShopService;

    // 获取所有精油分类
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(oilShopService.getAllCategories());
    }

    // 获取精油列表（支持可选分类参数）
    @GetMapping("/oils")
    public ResponseEntity<List<EssentialOil>> getOils(@RequestParam(name = "category", required = false) String category) {
        return ResponseEntity.ok(oilShopService.getVisibleOilsByCategory(category));
    }

    @PostMapping("/order")
    public ResponseEntity<OilOrderRes> createOrder(@RequestBody OilOrderReq req) {
        return ResponseEntity.ok(oilShopService.createOrder(req));
    }
    
    @GetMapping("/oils/{id}")
    public ResponseEntity<EssentialOil> getOilById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(oilShopService.getOilById(id));
    }
    
    @GetMapping("/orders/user/{userId}")
    public ResponseEntity<List<OilOrder>> getUserOrders(@PathVariable("userId") Integer userId) {
        return ResponseEntity.ok(oilShopService.getOrdersByUserId(userId));
    }
}