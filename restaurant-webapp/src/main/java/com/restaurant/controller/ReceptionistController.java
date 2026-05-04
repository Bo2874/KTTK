package com.restaurant.controller;

import com.restaurant.entity.BookingOrder;
import com.restaurant.entity.Employee;
import com.restaurant.facade.BookingFacade;
import com.restaurant.service.BookingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/reception")
public class ReceptionistController {

	@Autowired
	private BookingFacade bookingFacade;

	@Autowired
	private BookingService bookingService;

	@GetMapping("/home")
	public String showReceptionHome(HttpSession session) {
		if (!isReceptionist(session)) {
			return "redirect:/login";
		}
		return "reception/home";
	}

	@GetMapping("/dashboard")
	public String getPendingBookings(HttpSession session, Model model) {
		if (!isReceptionist(session)) {
			return "redirect:/login";
		}

		model.addAttribute("mode", "pending");
		model.addAttribute("bookings", bookingService.getPendingBookings());
		model.addAttribute("pageTitle", "Danh sách đơn chờ xác nhận");
		return "reception/dashboard";
	}

	@GetMapping("/orders")
	public String getAllBookings(HttpSession session, Model model) {
		if (!isReceptionist(session)) {
			return "redirect:/login";
		}

		model.addAttribute("mode", "all");
		model.addAttribute("bookings", bookingService.getAllBookings());
		model.addAttribute("pageTitle", "Toàn bộ đơn gửi đến");
		return "reception/dashboard";
	}

	@GetMapping("/order/{bookingId}")
	public String viewOrderDetail(@PathVariable Long bookingId,
								@RequestParam(value = "mode", required = false) String mode,
								HttpSession session,
								Model model) {
		if (!isReceptionist(session)) {
			return "redirect:/login";
		}

		BookingOrder booking = bookingService.findById(bookingId).orElse(null);
		if (booking == null) {
			return redirectByMode(mode);
		}

		String currentMode = "all".equalsIgnoreCase(mode) ? "all" : "pending";
		model.addAttribute("mode", currentMode);
		model.addAttribute("booking", booking);
		return "reception/order-detail";
	}

	@PostMapping("/confirm/{bookingId}")
	public String confirmBooking(@PathVariable Long bookingId,
							 @RequestParam(value = "mode", required = false) String mode,
							 HttpSession session,
							 RedirectAttributes redirectAttributes) {
		if (!isReceptionist(session)) {
			return "redirect:/login";
		}

		Employee employee = getAuthenticatedEmployee(session);
		if (employee == null || employee.getId() == null) {
			return "redirect:/login";
		}

		boolean confirmed = bookingFacade.confirmBookingAndUpdateTable(bookingId, employee.getId());
		if (confirmed) {
			redirectAttributes.addFlashAttribute("successMessage", "Xác nhận đơn thành công.");
		} else {
			redirectAttributes.addFlashAttribute("errorMessage", "Không thể xác nhận: khung giờ của bàn đã trùng với đơn đã được xác nhận.");
		}
		return redirectByMode(mode);
	}

	@PostMapping("/reject/{bookingId}")
	public String rejectBooking(@PathVariable Long bookingId,
							@RequestParam(value = "mode", required = false) String mode,
							HttpSession session) {
		if (!isReceptionist(session)) {
			return "redirect:/login";
		}

		Employee employee = getAuthenticatedEmployee(session);
		if (employee == null || employee.getId() == null) {
			return "redirect:/login";
		}

		bookingService.findById(bookingId).ifPresent(booking -> {
			if ("CHO_XAC_NHAN".equals(booking.getStatus())) {
				bookingFacade.rejectBooking(bookingId, employee.getId());
			}
		});
		return redirectByMode(mode);
	}

	private boolean isReceptionist(HttpSession session) {
		if (session == null) {
			return false;
		}
		Object role = session.getAttribute("userRole");
		if (!(role instanceof String roleValue)) {
			return false;
		}
		return "RECEPTIONIST".equals(roleValue)
				&& session.getAttribute("AUTH_EMPLOYEE") instanceof Employee;
	}

	private Employee getAuthenticatedEmployee(HttpSession session) {
		Object value = session.getAttribute("AUTH_EMPLOYEE");
		if (value instanceof Employee employee) {
			return employee;
		}
		return null;
	}

	private String redirectByMode(String mode) {
		if ("all".equalsIgnoreCase(mode)) {
			return "redirect:/reception/orders";
		}
		return "redirect:/reception/dashboard";
	}
}
