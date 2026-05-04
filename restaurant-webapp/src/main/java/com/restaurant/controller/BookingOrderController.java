package com.restaurant.controller;

import com.restaurant.entity.BookingOrder;
import com.restaurant.entity.Customer;
import com.restaurant.entity.Dish;
import com.restaurant.entity.RestaurantTable;
import com.restaurant.facade.BookingFacade;
import com.restaurant.service.BookingService;
import com.restaurant.service.DishService;
import com.restaurant.service.TableService;
import com.restaurant.util.TimeValidator;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/booking")
public class BookingOrderController {

	@Autowired
	private BookingFacade bookingFacade;

	@Autowired
	private TableService tableService;

	@Autowired
	private BookingService bookingService;

	@Autowired
	private DishService dishService;

	@Autowired
	private TimeValidator timeValidator;

	@GetMapping("/home")
	public String showCustomerHome(HttpSession session) {
		if (!isCustomer(session)) {
			return "redirect:/login";
		}
		return "booking/home";
	}

	@GetMapping("/time-selection")
	public String showTimeSelection(HttpSession session) {
		if (!isCustomer(session)) {
			return "redirect:/login";
		}
		return "booking/time-selection";
	}

	@PostMapping("/find-tables")
	public String findAvailableTables(@RequestParam String date,
								  @RequestParam String time,
								  HttpSession session,
								  Model model) {
		if (!isCustomer(session)) {
			return "redirect:/login";
		}

		if (!timeValidator.isValidBookingTime(date, time)) {
			model.addAttribute("error", "Thời gian đặt không hợp lệ. Vui lòng chọn từ thời điểm hiện tại trở đi và khung giờ đến từ 08:00 đến 21:00.");
			model.addAttribute("selectedDate", date);
			model.addAttribute("selectedTime", time);
			return "booking/time-selection";
		}

		return renderTableAndDishSelection(date, time, model);
	}

	@PostMapping("/submit")
	public String submitBooking(@RequestParam("bookingDate") String bookingDate,
								@RequestParam("bookingTime") String bookingTime,
								@RequestParam(value = "tableIds", required = false) List<Long> tableIds,
								@RequestParam MultiValueMap<String, String> formData,
								HttpSession session,
								Model model) {
		if (!isCustomer(session)) {
			return "redirect:/login";
		}

		Customer customer = getAuthenticatedCustomer(session);
		if (customer == null) {
			return "redirect:/login";
		}

		if (!timeValidator.isValidBookingTime(bookingDate, bookingTime)) {
			return "redirect:/booking/time-selection";
		}

		if (tableIds == null || tableIds.isEmpty()) {
			return "redirect:/booking/time-selection";
		}

		LocalDate parsedBookingDate;
		LocalTime parsedBookingTime;
		try {
			parsedBookingDate = LocalDate.parse(bookingDate);
			parsedBookingTime = parseBookingTime(bookingTime);
		} catch (Exception ex) {
			return "redirect:/booking/time-selection";
		}

		Set<Long> availableTableIds = tableService.getAvailableTables(
				bookingDate,
				bookingTime)
				.stream()
				.map(RestaurantTable::getId)
				.collect(Collectors.toSet());

		List<RestaurantTable> selectedTables = new ArrayList<>();
		for (Long tableId : tableIds.stream().distinct().toList()) {
			if (tableId == null || !availableTableIds.contains(tableId)) {
				continue;
			}
			tableService.findById(tableId).ifPresent(selectedTables::add);
		}

		if (selectedTables.isEmpty()) {
			return "redirect:/booking/time-selection";
		}

		try {
			BookingOrder bookingOrder = bookingFacade.createBookingForCustomer(
					customer,
					parsedBookingDate,
					parsedBookingTime,
					selectedTables,
					formData);

			session.setAttribute("LAST_BOOKING_ID", bookingOrder.getId());
			return "redirect:/booking/orders";
		} catch (RuntimeException ex) {
			model.addAttribute("error", "Không thể gửi yêu cầu đặt bàn lúc này. Vui lòng thử lại.");
			return renderTableAndDishSelection(bookingDate, bookingTime, model);
		}
	}

	@GetMapping("/orders")
	public String viewCustomerOrders(HttpSession session,
								 Model model) {
		if (!isCustomer(session)) {
			return "redirect:/login";
		}

		Customer customer = getAuthenticatedCustomer(session);
		if (customer == null) {
			return "redirect:/login";
		}

		model.addAttribute("customer", customer);
		model.addAttribute("bookings", bookingService.findByCustomerId(customer.getId()));
		return "booking/orders";
	}

	@GetMapping("/order/{bookingId}")
	public String viewBookingDetail(@PathVariable Long bookingId,
							 HttpSession session,
							 Model model) {
		if (!isCustomer(session)) {
			return "redirect:/login";
		}

		Customer customer = getAuthenticatedCustomer(session);
		if (customer == null) {
			return "redirect:/login";
		}

		BookingOrder booking = bookingService.findById(bookingId).orElse(null);
		if (booking == null) {
			return "redirect:/booking/orders";
		}

		if (booking.getCustomer() == null || !customer.getId().equals(booking.getCustomer().getId())) {
			return "redirect:/booking/orders";
		}

		model.addAttribute("booking", booking);
		return "booking/order-detail";
	}

	@GetMapping("/status")
	public String viewBookingStatus(@RequestParam(value = "phone", required = false) String phone,
								HttpSession session,
								Model model) {
		if (!isCustomer(session)) {
			return "redirect:/login";
		}

		Customer customer = getAuthenticatedCustomer(session);
		if (customer == null) {
			return "redirect:/login";
		}

		String lookupPhone = (phone != null && !phone.isBlank()) ? phone : customer.getPhone();

		model.addAttribute("booking", bookingService.findLatestByPhone(lookupPhone).orElse(null));
		model.addAttribute("phone", lookupPhone);
		return "booking/status";
	}

	private boolean isCustomer(HttpSession session) {
		return "CUSTOMER".equals(session.getAttribute("userRole"))
				&& session.getAttribute("AUTH_CUSTOMER") instanceof Customer;
	}

	private Customer getAuthenticatedCustomer(HttpSession session) {
		Object value = session.getAttribute("AUTH_CUSTOMER");
		if (value instanceof Customer customer) {
			return customer;
		}
		return null;
	}

	private LocalTime parseBookingTime(String time) {
		String normalized = time == null ? "" : time.trim();
		if (normalized.length() == 5) {
			normalized = normalized + ":00";
		}
		return LocalTime.parse(normalized);
	}

	private String renderTableAndDishSelection(String date, String time, Model model) {
		List<RestaurantTable> allTables = tableService.findAllTables();
		Set<Long> availableTableIds = tableService.getAvailableTables(date, time)
				.stream()
				.map(RestaurantTable::getId)
				.collect(Collectors.toSet());

		List<Dish> availableDishes = dishService.findDishes(null, null)
				.stream()
				.filter(dish -> Boolean.TRUE.equals(dish.getIsAvailable()))
				.collect(Collectors.toList());

		model.addAttribute("allTables", allTables);
		model.addAttribute("availableTableIds", availableTableIds);
		model.addAttribute("dishes", availableDishes);
		model.addAttribute("date", date);
		model.addAttribute("time", time);
		return "booking/table-selection";
	}
}