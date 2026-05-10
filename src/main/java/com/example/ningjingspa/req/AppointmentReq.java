package com.example.ningjingspa.req;

import java.time.LocalDateTime;

public class AppointmentReq {

	private Integer userId;
    private LocalDateTime appointmentTime;
    private Integer productId;
    private Integer oilId;
    
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public LocalDateTime getAppointmentTime() {
		return appointmentTime;
	}
	public void setAppointmentTime(LocalDateTime appointmentTime) {
		this.appointmentTime = appointmentTime;
	}
	public Integer getProductId() {
		return productId;
	}
	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	public Integer getOilId() {
		return oilId;
	}
	public void setOilId(Integer oilId) {
		this.oilId = oilId;
	}
    
    
}
