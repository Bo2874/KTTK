package com.restaurant.dao;

import com.restaurant.entity.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeDAO {
	Optional<Employee> findById(Long id);
	Optional<Employee> findByEmployeeCode(String employeeCode);
	Optional<Employee> findByUsername(String username);
	Employee save(Employee employee);
	List<Employee> findAll();
}
