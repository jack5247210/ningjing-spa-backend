package com.example.ningjingspa.req;

import java.util.List;

public class OilOrderReq {
    private Integer userId;
    private List<OilItem> items;
    private String address;
    private String recipientName;
    private String phone;
    private String deliveryMethod;
    private String paymentMethod;
    

    public static class OilItem {
        private Integer oilId;
        private Integer quantity;
        
		public Integer getOilId() {
			return oilId;
		}
		public void setOilId(Integer oilId) {
			this.oilId = oilId;
		}
		public Integer getQuantity() {
			return quantity;
		}
		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}
        
        
    }
    // getters & setters

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
