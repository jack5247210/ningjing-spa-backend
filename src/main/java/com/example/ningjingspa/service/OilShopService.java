package com.example.ningjingspa.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ningjingspa.dao.EssentialOilDao;
import com.example.ningjingspa.dao.OilOrderDao;
import com.example.ningjingspa.dao.OilOrderItemDao;
import com.example.ningjingspa.entity.EssentialOil;
import com.example.ningjingspa.entity.OilOrder;
import com.example.ningjingspa.entity.OilOrderItem;
import com.example.ningjingspa.req.OilOrderReq;
import com.example.ningjingspa.res.OilOrderRes;

import jakarta.transaction.Transactional;

@Service
public class OilShopService {

    @Autowired private EssentialOilDao oilDao;
    @Autowired private OilOrderDao orderDao;
    @Autowired private OilOrderItemDao orderItemDao;

    @Transactional
    public OilOrderRes createOrder(OilOrderReq req) {
        // 1. 計算總金額
        int total = 0;
        List<OilOrderItem> items = new ArrayList<>();
        for (OilOrderReq.OilItem item : req.getItems()) {
            EssentialOil oil = oilDao.findById(item.getOilId())
                .orElseThrow(() -> new RuntimeException("精油不存在"));
            if (oil.getStock() < item.getQuantity()) {
                throw new RuntimeException(oil.getName() + " 庫存不足");
            }
            int subtotal = oil.getPrice() * item.getQuantity();
            total += subtotal;
            // 扣庫存
            oil.setStock(oil.getStock() - item.getQuantity());
            oilDao.save(oil);

            OilOrderItem oi = new OilOrderItem();
            oi.setOilId(item.getOilId());
            oi.setQuantity(item.getQuantity());
            oi.setUnitPrice(oil.getPrice());
            items.add(oi);
        }

        // 2. 滿額優惠規則
        int discount = 0;
        String gift = null;
        if (total >= 2000) {
            discount = 200;      // 滿2000折200
        } else if (total >= 1000) {
            gift = "薰衣草精油滾珠瓶 5ml";   // 滿1000送小禮物
        }

        int finalAmount = total - discount;

        // 3. 建立訂單
        OilOrder order = new OilOrder();
        order.setUserId(req.getUserId());
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(total);
        order.setDiscount(discount);
        order.setFinalAmount(finalAmount);
        order.setGiftItem(gift);
        order.setStatus("pending");
        order.setAddress(req.getAddress());
        order.setDeliveryMethod(req.getDeliveryMethod());
        order.setPaymentMethod(req.getPaymentMethod());
        order.setRecipientName(req.getRecipientName());
        order.setPhone(req.getPhone());
        OilOrder savedOrder = orderDao.save(order);

        // 4. 儲存明細 (設定 orderId)
        for (OilOrderItem item : items) {
            item.setOrderId(savedOrder.getOrderId());
            orderItemDao.save(item);
        }

        // 5. 回傳結果
        OilOrderRes res = new OilOrderRes();
        res.setOrderId(savedOrder.getOrderId());
        res.setFinalAmount(finalAmount);
        res.setGiftItem(gift);
        res.setDiscount(discount);
        return res;
    }

    // 前台获取精油列表：直接返回全部（不再区分可见性）
    public List<EssentialOil> getVisibleOils() {
        return oilDao.findAll();
    }

    // 管理员获取全部精油
    public List<EssentialOil> getAllOilsForAdmin() {
        return oilDao.findAll();
    }

    public EssentialOil getOilById(Integer id) {
        return oilDao.findById(id).orElseThrow(() -> new RuntimeException("精油不存在"));
    }

    // 新增精油：不再设置 isVisible
    public EssentialOil createOil(EssentialOil oil) {
        return oilDao.save(oil);
    }

    public EssentialOil updateOil(Integer id, EssentialOil oilData) {
        EssentialOil oil = getOilById(id);
        oil.setName(oilData.getName());
        oil.setPrice(oilData.getPrice());
        oil.setCapacity(oilData.getCapacity());
        oil.setDescription(oilData.getDescription());
        oil.setIntroduce(oilData.getIntroduce());
        oil.setOilImg(oilData.getOilImg());
        oil.setStock(oilData.getStock());
        oil.setCategory(oilData.getCategory());
        return oilDao.save(oil);
    }

    public void deleteOil(Integer id) {
        oilDao.deleteById(id);
    }

    // 取得所有訂單（含使用者姓名、訂單項目）
    public List<OilOrder> getAllOrders() {
        return orderDao.findAllByOrderByOrderDateDesc();
    }

    // 更新訂單狀態
    @Transactional
    public void updateOrderStatus(Integer orderId, String status) {
        OilOrder order = orderDao.findById(orderId)
                .orElseThrow(() -> new RuntimeException("訂單不存在"));
        order.setStatus(status);
        orderDao.save(order);
    }

    // 取得單筆訂單並載入訂單項目
    public OilOrder getOrderWithItems(Integer orderId) {
        OilOrder order = orderDao.findById(orderId)
                .orElseThrow(() -> new RuntimeException("訂單不存在"));
        List<OilOrderItem> items = orderItemDao.findByOrderId(orderId);
        for (OilOrderItem item : items) {
            oilDao.findById(item.getOilId()).ifPresent(oil -> {
                item.setOilName(oil.getName());
            });
        }
        order.setItems(items);
        return order;
    }

    // 获取所有分类
    public List<String> getAllCategories() {
        return oilDao.findDistinctCategories();
    }

    // 根据分类获取精油（前台用）
    public List<EssentialOil> getVisibleOilsByCategory(String category) {
        if (category == null || category.isEmpty()) {
            return oilDao.findAll();
        }
        return oilDao.findByCategory(category);
    }

    // 管理员用：根据分类获取所有精油
    public List<EssentialOil> getOilsByCategory(String category) {
        if (category == null || category.isEmpty()) {
            return oilDao.findAll();
        }
        return oilDao.findByCategory(category);
    }
    
    public List<OilOrder> getOrdersByUserId(Integer userId) {
        return orderDao.findByUserIdOrderByOrderDateDesc(userId);
    }
}