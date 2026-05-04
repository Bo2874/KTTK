package com.restaurant.service;

import com.restaurant.dao.BookingOrderDAO;
import com.restaurant.entity.BookingOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingOrderDAO bookingOrderDAO;

    @Transactional
    public BookingOrder createPendingBooking(BookingOrder bookingOrder) {
        if (bookingOrder.getStatus() == null || bookingOrder.getStatus().isBlank()) {
            bookingOrder.setStatus("CHO_XAC_NHAN");
        }
        bookingOrderDAO.insert(bookingOrder);
        return bookingOrder;
    }

    @Transactional
    public void updateBookingStatus(Long bookingId, String status) {
        bookingOrderDAO.updateBookingStatus(bookingId, status);
    }

	@Transactional
	public void updateBookingStatusAndEmployee(Long bookingId, String status, Long employeeId) {
		bookingOrderDAO.updateBookingStatusAndEmployee(bookingId, status, employeeId);
	}

    @Transactional(readOnly = true)
    public Optional<BookingOrder> findById(Long bookingId) {
        return bookingOrderDAO.findById(bookingId);
    }

    @Transactional(readOnly = true)
    public List<BookingOrder> getPendingBookings() {
        return bookingOrderDAO.findByStatus("CHO_XAC_NHAN");
    }

    @Transactional(readOnly = true)
    public List<BookingOrder> getAllBookings() {
        return bookingOrderDAO.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<BookingOrder> findLatestByPhone(String phone) {
        return bookingOrderDAO.findLatestByCustomerPhone(phone);
    }

    @Transactional(readOnly = true)
    public List<BookingOrder> findByPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return List.of();
        }
        return bookingOrderDAO.findByCustomerPhone(phone);
    }

    @Transactional(readOnly = true)
    public List<BookingOrder> findByCustomerId(Long customerId) {
        if (customerId == null) {
            return List.of();
        }
        return bookingOrderDAO.findByCustomerId(customerId);
    }
}
