package com.example.ningjingspa.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "consult")
public class Consult {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "consult_id")
	private int consultId;
	
	@Column(name = "user_id")
	private Integer userId;
	
	@Column(name = "selected_tags")
	private String tags;
	
	@Column(name = "product_id")
	private Integer productId;
	
	@Column(name = "ai_reason")
	private String aiReason;
	
	@Column(name = "created_date")
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
