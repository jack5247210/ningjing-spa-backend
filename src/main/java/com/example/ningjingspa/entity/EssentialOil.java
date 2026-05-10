package com.example.ningjingspa.entity;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "essential_oil")
public class EssentialOil {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "oil_id")
    private Integer oilId;

	@Column(name = "name")
    private String name;
	
	@Column(name = "price")
    private Integer price;
	
	@Column(name = "capacity")
    private String capacity;
	
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "introduce", columnDefinition = "TEXT")
    private String introduce;
    
    @Column(name = "oil_img")
    private String oilImg;
    
    @Column(name = "stock")
    private Integer stock;
    
    @Column(name = "category")
    private String category;

	public Integer getOilId() {
		return oilId;
	}

	public void setOilId(Integer oilId) {
		this.oilId = oilId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public String getCapacity() {
		return capacity;
	}

	public void setCapacity(String capacity) {
		this.capacity = capacity;
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

	public String getOilImg() {
		return oilImg;
	}

	public void setOilImg(String oilImg) {
		this.oilImg = oilImg;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
    
    
}
