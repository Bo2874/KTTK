package com.restaurant.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_booking_order")
public class BookingOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "order_code", nullable = false, unique = true)
	private String orderCode;

	@Column(name = "booking_date", nullable = false)
	private LocalDate bookingDate;

	@Column(name = "arrival_time", nullable = false)
	private LocalTime arrivalTime;

	@Column(name = "end_time", nullable = false)
	private LocalTime endTime;

	@Column(name = "total_amount", nullable = false, precision = 19, scale = 0)
	private BigDecimal totalAmount = BigDecimal.ZERO;

	@Column(name = "status", nullable = false)
	private String status;

	@Column(name = "note", length = 255)
	private String note;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id", nullable = false)
	private Customer customer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee_id")
	private Employee employee;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = "tbl_booking_order_table",
			joinColumns = @JoinColumn(name = "tbl_booking_order_id"),
			inverseJoinColumns = @JoinColumn(name = "tbl_restaurant_table_id")
	)
	private List<RestaurantTable> selectedTables = new ArrayList<>();

	@OneToMany(mappedBy = "bookingOrder", fetch = FetchType.EAGER)
	private List<OrderDetail> orderDetails = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public LocalDate getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(LocalDate bookingDate) {
		this.bookingDate = bookingDate;
	}

	public LocalTime getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(LocalTime arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public LocalTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public List<RestaurantTable> getSelectedTables() {
		return selectedTables;
	}

	public void setSelectedTables(List<RestaurantTable> selectedTables) {
		this.selectedTables = selectedTables;
	}

	public List<OrderDetail> getOrderDetails() {
		return orderDetails;
	}

	public void setOrderDetails(List<OrderDetail> orderDetails) {
		this.orderDetails = orderDetails;
	}

	public void addOrderDetail(OrderDetail detail) {
		this.orderDetails.add(detail);
		detail.setBookingOrder(this);
	}
}