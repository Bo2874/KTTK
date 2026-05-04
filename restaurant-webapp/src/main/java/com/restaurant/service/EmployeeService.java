package com.restaurant.service;

import com.restaurant.entity.Employee;

import java.util.Optional;

public interface EmployeeService {
	Optional<Employee> findByUsername(String username);
	Employee saveEmployee(Employee employee);
}
