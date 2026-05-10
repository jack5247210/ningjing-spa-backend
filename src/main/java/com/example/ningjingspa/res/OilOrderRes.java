package com.example.ningjingspa.res;

import java.util.List;

import com.example.ningjingspa.req.OilOrderReq.OilItem;

public class OilOrderRes {
    private Integer orderId;
    private Integer finalAmount;
    private String giftItem;
    private Integer discount;
    private Integer userId;
    private List<OilItem> items;
    private String address;
    private String recipientName;
    private String phone;
    private String deliveryMethod;
    private String paymentMethod;
    // 可包含訂單明細
	public Integer getOrderId() {
		return orderId;
	}
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	public Integer getFinalAmount() {
		return finalAmount;
	}
	public void setFinalAmount(Integer finalAmount) {
		this.finalAmount = finalAmount;
	}
	public String getGiftItem() {
		return giftItem;
	}
	public void setGiftItem(String giftItem) {
		this.giftItem = giftItem;
	}
	public Integer getDiscount() {
		return discount;
	}
	public void setDiscount(Integer discount) {
		this.discount = discount;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public List<OilItem> getItems() {
		return items;
	}
	public void setItems(List<OilItem> items) {
		this.items = items;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getRecipientName() {
		return recipientName;
	}
	public void setRecipientName(String recipientName) {
		this.recipientName = recipientName;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getDeliveryMethod() {
		return deliveryMethod;
	}
	public void setDeliveryMethod(String deliveryMethod) {
		this.deliveryMethod = deliveryMethod;
	}
	public String getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
    
    
}
