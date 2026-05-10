package com.example.ningjingspa.controller;

import com.example.ningjingspa.entity.OilOrder;
import com.example.ningjingspa.service.OilShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
public class OilOrderAdminController {

    @Autowired
    private OilShopService oilShopService;

    // 取得所有訂單（依日期倒序）
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<OilOrder>> getAllOrders() {
        return ResponseEntity.ok(oilShopService.getAllOrders());
    }

    // 更新訂單狀態
    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable("orderId") Integer orderId,
                                                  @RequestParam("status") String status) {
        oilShopService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok().build();
    }

    // 取得單筆訂單明細（包含訂單項目）
    @GetMapping("/{orderId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<OilOrder> getOrderDetail(@PathVariable("orderId") Integer orderId) {
        return ResponseEntity.ok(oilShopService.getOrderWithItems(orderId));
    }
}