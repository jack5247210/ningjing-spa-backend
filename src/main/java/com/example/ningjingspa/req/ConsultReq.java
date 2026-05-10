package com.example.ningjingspa.req;

public class ConsultReq {

	private Integer userId;
	private String tags;
	private Integer productId;
	private String aiReason;
	
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
}
