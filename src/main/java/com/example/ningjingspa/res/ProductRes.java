package com.example.ningjingspa.res;

public class ProductRes {

	private int productId;
    private String title;
    private String description;
    private String introduce;   // 新增
    private int duration;
    private int price;
    private String productImg;
    
    // 你可以增加一些方便前端顯示的欄位（不一定要存在資料庫）
    // 例如：顯示「60 分鐘 / $2,000」的標籤
    public String getDisplayInfo() {
        return this.duration + " 分鐘 / $" + this.price;
    }

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	

	public String getIntroduce() {
		return introduce;
	}

	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getProductImg() {
		return productImg;
	}

	public void setProductImg(String productImg) {
		this.productImg = productImg;
	}

	
    
}
