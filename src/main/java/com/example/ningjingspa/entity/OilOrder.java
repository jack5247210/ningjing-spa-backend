package com.example.ningjingspa.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "oil_order")
public class OilOrder {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_id")
    private Integer orderId;
	
	@OneToMany
	@JoinColumn(name = "order_id")
	private List<OilOrderItem> items;
	
	@Column(name = "user_id")
    private Integer userId;
	
	@Column(name = "order_date")
    private LocalDateTime orderDate;
	
	@Column(name = "total_amount")
	private Integer totalAmount;
    
	@Column(name = "discount")
	private Integer discount;
	
	@Column(name = "final_amount")
    private Integer finalAmount;
	
	@Column(name = "gift_item")
	private String giftItem;
	
	@Column(name = "status")
    private String status;
	
	@Column(name = "address")
    private String address;
	
	@Column(name = "delivery_method")
    private String deliveryMethod;  // pickup, delivery

    @Column(name = "payment_method")
    private String paymentMethod;   // cod, etc.

    @Column(name = "recipient_name")
    private String recipientName;

    @Column(name = "phone")
    private String phone;

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public LocalDateTime getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(LocalDateTime orderDate) {
		this.orderDate = orderDate;
	}

	public Integer getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Integer totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Integer getDiscount() {
		return discount;
	}

	public void setDiscount(Integer discount) {
		this.discount = discount;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	public List<OilOrderItem> getItems() {
		return items;
	}

	public void setItems(List<OilOrderItem> items) {
		this.items = items;
	}
	
	
}
