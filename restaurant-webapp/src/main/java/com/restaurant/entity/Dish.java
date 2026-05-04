package com.restaurant.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "tbl_dish")
public class Dish {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "dish_code", nullable = false, unique = true, length = 50)
	private String dishCode;

	@Column(name = "name", nullable = false, length = 150)
	private String name;

	@Column(name = "description", nullable = false, length = 255)
	private String description;

	@Column(name = "image_url", nullable = false, length = 255)
	private String imageUrl;

	@Column(name = "category", nullable = false, length = 80)
	private String category;

	@Column(name = "price", nullable = false, precision = 19, scale = 0)
	private BigDecimal price;

	@Column(name = "is_available", nullable = false)
	private Boolean isAvailable = Boolean.TRUE;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDishCode() {
		return dishCode;
	}

	public void setDishCode(String dishCode) {
		this.dishCode = dishCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Boolean getIsAvailable() {
		return isAvailable;
	}

	public void setIsAvailable(Boolean available) {
		isAvailable = available;
	}
}
