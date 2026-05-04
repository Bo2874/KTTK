package com.restaurant.facade;

import com.restaurant.dao.BookingOrderDAO;
import com.restaurant.dao.CustomerDAO;
import com.restaurant.dao.OrderDetailDAO;
import com.restaurant.entity.BookingOrder;
import com.restaurant.entity.Customer;
import com.restaurant.entity.Dish;
import com.restaurant.entity.OrderDetail;
import com.restaurant.entity.RestaurantTable;
import com.restaurant.service.BookingService;
import com.restaurant.service.DishService;
import com.restaurant.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class BookingFacade {

	private static final int DEFAULT_BOOKING_DURATION_HOURS = 2;

	@Autowired
	private BookingService bookingService;

	@Autowired
	private BookingOrderDAO bookingOrderDAO;

	@Autowired
	private OrderDetailDAO orderDetailDAO;

	@Autowired
	private CustomerDAO customerDAO;

	@Autowired
	private DishService dishService;

	@Autowired
	private TableService tableService;

	@Transactional
	public BookingOrder createBookingForCustomer(Customer customer,
											 LocalDate bookingDate,
											 LocalTime arrivalTime,
											 List<RestaurantTable> selectedTables,
											 MultiValueMap<String, String> formData) {

		if (customer == null || customer.getId() == null) {
			throw new IllegalArgumentException("Khách hàng không hợp lệ");
		}

		Customer managedCustomer = customerDAO.findById(customer.getId())
				.orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách hàng"));

		List<RestaurantTable> managedSelectedTables = selectedTables == null
				? List.of()
				: selectedTables.stream()
						.map(RestaurantTable::getId)
						.filter(id -> id != null)
						.distinct()
						.map(id -> tableService.findById(id).orElse(null))
						.filter(table -> table != null)
						.toList();

		if (managedSelectedTables.isEmpty()) {
			throw new IllegalArgumentException("Chưa chọn bàn hợp lệ");
		}

		BookingOrder bookingOrder = new BookingOrder();
		bookingOrder.setOrderCode(generateOrderCode());
		bookingOrder.setBookingDate(bookingDate);
		bookingOrder.setArrivalTime(arrivalTime);
		bookingOrder.setEndTime(arrivalTime.plusHours(DEFAULT_BOOKING_DURATION_HOURS));
		bookingOrder.setStatus("CHO_XAC_NHAN");
		bookingOrder.setCustomer(managedCustomer);
		bookingOrder.setSelectedTables(new ArrayList<>(managedSelectedTables));

		Map<Long, Integer> dishQuantities = new LinkedHashMap<>();
		for (RestaurantTable selectedTable : managedSelectedTables) {
			List<String> dishIdValues = formData.get("dishIdsByTable_" + selectedTable.getId());
			if (dishIdValues == null) {
				continue;
			}

			for (String dishIdValue : dishIdValues) {
				try {
					Long dishId = Long.valueOf(dishIdValue);
					Dish dish = dishService.getDishById(dishId);
					if (dish != null && Boolean.TRUE.equals(dish.getIsAvailable())) {
						dishQuantities.merge(dishId, 1, Integer::sum);
					}
				} catch (NumberFormatException ignored) {
					// Skip invalid dish ids from request.
				}
			}
		}

		bookingOrderDAO.insert(bookingOrder);

		BigDecimal totalAmount = BigDecimal.ZERO;
		List<OrderDetail> orderDetails = new ArrayList<>();
		for (Map.Entry<Long, Integer> entry : dishQuantities.entrySet()) {
			Long dishId = entry.getKey();
			Integer quantity = entry.getValue();

			Dish dish = dishService.getDishById(dishId);
			if (dish == null || dish.getPrice() == null || quantity == null || quantity <= 0) {
				continue;
			}

			OrderDetail detail = new OrderDetail();
			detail.setBookingOrder(bookingOrder);
			detail.setDish(dish);
			detail.setQuantity(quantity);
			detail.setUnitPrice(dish.getPrice());
			BigDecimal subtotal = dish.getPrice().multiply(BigDecimal.valueOf(quantity));
			detail.setSubtotal(subtotal);
			orderDetails.add(detail);
			totalAmount = totalAmount.add(subtotal);
		}

		bookingOrder.setTotalAmount(totalAmount);

		if (orderDetails.isEmpty()) {
			bookingOrder.setNote("Khách chưa chọn món");
		} else {
			bookingOrder.setNote("Số món trong đơn: " + orderDetails.size());
		}

		bookingOrderDAO.update(bookingOrder);
		if (!orderDetails.isEmpty()) {
			orderDetailDAO.insertAll(orderDetails);
			bookingOrder.setOrderDetails(orderDetails);
		}

		return bookingOrder;
	}

	@Transactional
	public void processNewBooking(BookingOrder bookingOrder) {
		bookingService.createPendingBooking(bookingOrder);
	}

	@Transactional
	public boolean confirmBookingAndUpdateTable(Long bookingId, Long employeeId) {
		BookingOrder booking = bookingService.findById(bookingId).orElse(null);
		if (booking == null || !"CHO_XAC_NHAN".equals(booking.getStatus())) {
			return false;
		}

		if (hasOverlapWithConfirmedBooking(booking)) {
			return false;
		}

		bookingService.updateBookingStatusAndEmployee(bookingId, "DA_XAC_NHAN", employeeId);
		booking.getSelectedTables().forEach(table -> {
			if (table != null && table.getId() != null) {
				tableService.updateTableStatus(table.getId(), "RESERVED");
			}
		});
		return true;
	}

	@Transactional
	public void rejectBooking(Long bookingId, Long employeeId) {
		bookingService.updateBookingStatusAndEmployee(bookingId, "TU_CHOI", employeeId);
		bookingService.findById(bookingId).ifPresent(booking -> {
			booking.getSelectedTables().forEach(table -> {
				if (table != null && table.getId() != null) {
					tableService.updateTableStatus(table.getId(), "AVAILABLE");
				}
			});
		});
	}

	private String generateOrderCode() {
		String timestamp = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
		long suffix = System.nanoTime() % 100000;
		return "OD" + timestamp + suffix;
	}

	private boolean hasOverlapWithConfirmedBooking(BookingOrder booking) {
		if (booking == null || booking.getBookingDate() == null || booking.getArrivalTime() == null) {
			return true;
		}

		LocalTime requestedEnd = booking.getEndTime() != null
				? booking.getEndTime()
				: booking.getArrivalTime().plusHours(DEFAULT_BOOKING_DURATION_HOURS);

		if (booking.getSelectedTables() == null || booking.getSelectedTables().isEmpty()) {
			return false;
		}

		for (RestaurantTable table : booking.getSelectedTables()) {
			if (table == null || table.getId() == null) {
				continue;
			}

			if (bookingOrderDAO.existsConfirmedOverlap(
					table.getId(),
					booking.getBookingDate(),
					booking.getArrivalTime(),
					requestedEnd,
					booking.getId())) {
				return true;
			}
		}

		return false;
	}
}
