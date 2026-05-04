package com.restaurant.dao;

import com.restaurant.entity.OrderDetail;

import java.util.List;

public interface OrderDetailDAO {
	void insert(OrderDetail orderDetail);
	void insertAll(List<OrderDetail> orderDetails);
	List<OrderDetail> findByBookingOrderId(Long bookingOrderId);
	void deleteByBookingOrderId(Long bookingOrderId);
}