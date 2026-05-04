package com.restaurant.controller;

import com.restaurant.dao.CustomerDAO;
import com.restaurant.dao.EmployeeDAO;
import com.restaurant.entity.Customer;
import com.restaurant.entity.Employee;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;

@Controller
public class AuthController {

	@Autowired
	private CustomerDAO customerDAO;

	@Autowired
	private EmployeeDAO employeeDAO;

	@GetMapping({"/", "/login"})
	public String showLoginForm(HttpSession session) {
		if (session != null && session.getAttribute("userRole") != null) {
			return redirectByRole(String.valueOf(session.getAttribute("userRole")));
		}
		return "login";
	}

	@PostMapping("/login")
	public String processLogin(@RequestParam("username") String username,
							   @RequestParam("password") String password,
							   HttpServletRequest request,
							   Model model) {

		String inputUsername = username == null ? "" : username.trim();
		String inputPassword = password == null ? "" : password.trim();

		if (inputUsername.isEmpty() || inputPassword.isEmpty()) {
			model.addAttribute("error", "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu");
			return "login";
		}

		Customer customer = customerDAO.findByUsername(inputUsername).orElse(null);
		if (customer != null && Objects.equals(customer.getPassword(), inputPassword)) {
			HttpSession newSession = resetSession(request);
			newSession.setAttribute("userRole", "CUSTOMER");
			newSession.setAttribute("AUTH_CUSTOMER", customer);
			newSession.setAttribute("username", customer.getUsername());
			newSession.setAttribute("LOGGED_IN_USER", customer.getFullName());
			return "redirect:/booking/home";
		}

		Employee employee = employeeDAO.findByUsername(inputUsername).orElse(null);
		if (employee != null && Objects.equals(employee.getPassword(), inputPassword)) {
			HttpSession newSession = resetSession(request);
			String role = normalizeEmployeeRole(employee.getRole());
			newSession.setAttribute("userRole", role);
			newSession.setAttribute("AUTH_EMPLOYEE", employee);
			newSession.setAttribute("username", employee.getUsername());
			newSession.setAttribute("LOGGED_IN_USER", employee.getFullName());
			return redirectByRole(role);
		}

		model.addAttribute("error", "Sai tên đăng nhập hoặc mật khẩu");
		return "login";
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		if (session != null) {
			session.invalidate();
		}
		return "redirect:/login";
	}

	private HttpSession resetSession(HttpServletRequest request) {
		HttpSession oldSession = request.getSession(false);
		if (oldSession != null) {
			oldSession.invalidate();
		}
		return request.getSession(true);
	}

	private String normalizeEmployeeRole(String role) {
		if (role == null || role.isBlank()) {
			return "RECEPTIONIST";
		}
		String normalized = role.trim().toUpperCase();
		return switch (normalized) {
			case "MANAGER", "RECEPTIONIST" -> normalized;
			default -> "RECEPTIONIST";
		};
	}

	private String redirectByRole(String role) {
		return switch (role) {
			case "CUSTOMER" -> "redirect:/booking/home";
			case "MANAGER" -> "redirect:/admin/dishes";
			case "RECEPTIONIST" -> "redirect:/reception/home";
			default -> "redirect:/login";
		};
	}
}