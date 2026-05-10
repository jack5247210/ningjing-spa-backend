package com.example.ningjingspa.res;

import java.time.LocalDateTime;

public class ConsultRes {

	private int consultId;
	private Integer userId;
	private String tags;
	private Integer productId;
	private String aiReason;
	private LocalDateTime createdDate;
	
	
	public int getConsultId() {
		return consultId;
	}
	public void setConsultId(int consultId) {
		this.consultId = consultId;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public Integer getProductId() {
		return productId;
	}
	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	public String getAiReason() {
		return aiReason;
	}
	public void setAiReason(String aiReason) {
		this.aiReason = aiReason;
	}
	public LocalDateTime getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}
}
