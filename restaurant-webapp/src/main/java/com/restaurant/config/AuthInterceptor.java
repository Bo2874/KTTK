package com.restaurant.config;

import com.restaurant.entity.Employee;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String path = request.getRequestURI();
		HttpSession session = request.getSession(false);

		if (path.startsWith("/booking")) {
			if (session != null && session.getAttribute("AUTH_CUSTOMER") != null) {
				return true;
			}
			response.sendRedirect("/login");
			return false;
		}

		if (path.startsWith("/reception")) {
			if (session != null && session.getAttribute("AUTH_EMPLOYEE") instanceof Employee employee) {
				String role = employee.getRole() == null ? "" : employee.getRole().trim().toUpperCase();
				if ("RECEPTIONIST".equals(role)) {
					return true;
				}
			}
			response.sendRedirect("/login");
			return false;
		}

		if (path.startsWith("/admin")) {
			if (session != null && session.getAttribute("AUTH_EMPLOYEE") instanceof Employee employee) {
				String role = employee.getRole() == null ? "" : employee.getRole().trim().toUpperCase();
				if ("MANAGER".equals(role)) {
					return true;
				}
			}
			response.sendRedirect("/login");
			return false;
		}

		return true;
	}
}