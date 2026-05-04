package com.restaurant.dao;

import com.restaurant.entity.BookingOrder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface BookingOrderDAO {
	void insert(BookingOrder bookingOrder);
	BookingOrder update(BookingOrder bookingOrder);
	void updateBookingStatus(Long bookingOrderId, String status);
	void updateBookingStatusAndEmployee(Long bookingOrderId, String status, Long employeeId);
	Optional<BookingOrder> findById(Long id);
	List<BookingOrder> findByStatus(String status);
	List<BookingOrder> findAll();
	Optional<BookingOrder> findLatestByCustomerPhone(String phone);
	List<BookingOrder> findByCustomerPhone(String phone);
	List<BookingOrder> findByCustomerId(Long customerId);
	boolean existsConfirmedOverlap(Long tableId,
								 LocalDate bookingDate,
								 LocalTime requestedStart,
								 LocalTime requestedEnd,
								 Long excludeBookingId);
}