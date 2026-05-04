package com.restaurant.service.impl;

import com.restaurant.dao.EmployeeDAO;
import com.restaurant.entity.Employee;
import com.restaurant.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	private EmployeeDAO employeeDAO;

	@Override
	@Transactional(readOnly = true)
	public Optional<Employee> findByUsername(String username) {
		return employeeDAO.findByUsername(username);
	}

	@Override
	@Transactional
	public Employee saveEmployee(Employee employee) {
		return employeeDAO.save(employee);
	}
}
