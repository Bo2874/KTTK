package com.restaurant.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TableDTO {

	private Long id;

	@NotBlank(message = "Mã bàn là bắt buộc")
	private String tableCode;

	@NotBlank(message = "Khu vực là bắt buộc")
	private String area;

	@NotNull(message = "Sức chứa là bắt buộc")
	@Min(value = 1, message = "Sức chứa phải lớn hơn hoặc bằng 1")
	private Integer capacity;

	@NotBlank(message = "Trạng thái là bắt buộc")
	private String status;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTableCode() {
		return tableCode;
	}

	public void setTableCode(String tableCode) {
		this.tableCode = tableCode;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public Integer getCapacity() {
		return capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
