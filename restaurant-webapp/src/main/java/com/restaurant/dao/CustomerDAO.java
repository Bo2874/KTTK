package com.restaurant.dao;

import com.restaurant.entity.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDAO {
	Optional<Customer> findById(Long id);
	Optional<Customer> findByUsername(String username);
	Optional<Customer> findByPhone(String phone);
	Customer save(Customer customer);
	List<Customer> findAll();
}