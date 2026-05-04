package com.restaurant.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class DishDTO {

	private Long id;

	@NotBlank(message = "Mã món là bắt buộc")
	private String dishCode;

	@NotBlank(message = "Tên món là bắt buộc")
	private String name;

	@NotBlank(message = "Danh mục là bắt buộc")
	private String category;

	@NotNull(message = "Giá bán là bắt buộc")
	@DecimalMin(value = "0.0", inclusive = false, message = "Giá bán phải lớn hơn 0")
	private BigDecimal price;

	private String description;

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

	public Boolean getIsAvailable() {
		return isAvailable;
	}

	public void setIsAvailable(Boolean available) {
		isAvailable = available;
	}
}
