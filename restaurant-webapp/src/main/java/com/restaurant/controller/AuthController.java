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

	@GetMapping("/register")
	public String showRegisterForm(HttpSession session) {
		if (session != null && session.getAttribute("userRole") != null) {
			return redirectByRole(String.valueOf(session.getAttribute("userRole")));
		}
		return "register";
	}

	@PostMapping("/register")
	public String processRegister(@RequestParam("fullName") String fullName,
								   @RequestParam("phone") String phone,
								   @RequestParam("email") String email,
								   @RequestParam("username") String username,
								   @RequestParam("password") String password,
								   @RequestParam("confirmPassword") String confirmPassword,
								   Model model) {

		String inputFullName = fullName == null ? "" : fullName.trim();
		String inputPhone = phone == null ? "" : phone.trim();
		String inputEmail = email == null ? "" : email.trim();
		String inputUsername = username == null ? "" : username.trim();
		String inputPassword = password == null ? "" : password.trim();
		String inputConfirm = confirmPassword == null ? "" : confirmPassword.trim();

		// Giữ lại giá trị đã nhập để hiển thị lại khi có lỗi
		model.addAttribute("fullName", inputFullName);
		model.addAttribute("phone", inputPhone);
		model.addAttribute("email", inputEmail);
		model.addAttribute("username", inputUsername);

		if (inputFullName.isEmpty() || inputPhone.isEmpty() || inputEmail.isEmpty()
				|| inputUsername.isEmpty() || inputPassword.isEmpty() || inputConfirm.isEmpty()) {
			model.addAttribute("error", "Vui lòng nhập đầy đủ tất cả các trường");
			return "register";
		}

		if (!inputPhone.matches("^0\\d{9,10}$")) {
			model.addAttribute("error", "Số điện thoại phải bắt đầu bằng 0 và có 10-11 chữ số");
			return "register";
		}

		if (!inputEmail.matches("^[\\w.+-]+@[\\w-]+\\.[\\w.-]+$")) {
			model.addAttribute("error", "Email không hợp lệ");
			return "register";
		}

		if (inputUsername.length() < 4) {
			model.addAttribute("error", "Tên đăng nhập phải có ít nhất 4 ký tự");
			return "register";
		}

		if (inputPassword.length() < 6) {
			model.addAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự");
			return "register";
		}

		if (!Objects.equals(inputPassword, inputConfirm)) {
			model.addAttribute("error", "Mật khẩu xác nhận không khớp");
			return "register";
		}

		if (customerDAO.findByUsername(inputUsername).isPresent()
				|| employeeDAO.findByUsername(inputUsername).isPresent()) {
			model.addAttribute("error", "Tên đăng nhập đã tồn tại");
			return "register";
		}

		if (customerDAO.findByPhone(inputPhone).isPresent()) {
			model.addAttribute("error", "Số điện thoại đã được sử dụng");
			return "register";
		}

		if (customerDAO.findByEmail(inputEmail).isPresent()) {
			model.addAttribute("error", "Email đã được sử dụng");
			return "register";
		}

		Customer customer = new Customer();
		customer.setFullName(inputFullName);
		customer.setPhone(inputPhone);
		customer.setEmail(inputEmail);
		customer.setUsername(inputUsername);
		customer.setPassword(inputPassword);
		customerDAO.save(customer);

		return "redirect:/login?registered=1";
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