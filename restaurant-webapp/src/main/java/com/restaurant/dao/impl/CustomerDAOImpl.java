package com.restaurant.dao.impl;

import com.restaurant.dao.CustomerDAO;
import com.restaurant.entity.Customer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CustomerDAOImpl implements CustomerDAO {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Optional<Customer> findById(Long id) {
		return Optional.ofNullable(entityManager.find(Customer.class, id));
	}

	@Override
	public Optional<Customer> findByUsername(String username) {
		try {
			Customer customer = entityManager.createQuery(
						"SELECT c FROM Customer c WHERE c.username = :username", Customer.class)
					.setParameter("username", username)
					.getSingleResult();
			return Optional.of(customer);
		} catch (NoResultException ex) {
			return Optional.empty();
		}
	}

	@Override
	public Optional<Customer> findByPhone(String phone) {
		try {
			Customer customer = entityManager.createQuery(
						"SELECT c FROM Customer c WHERE c.phone = :phone", Customer.class)
					.setParameter("phone", phone)
					.getSingleResult();
			return Optional.of(customer);
		} catch (NoResultException ex) {
			return Optional.empty();
		}
	}

	@Override
	public Customer save(Customer customer) {
		if (customer.getId() == null) {
			entityManager.persist(customer);
			return customer;
		}
		return entityManager.merge(customer);
	}

	@Override
	public List<Customer> findAll() {
		return entityManager.createQuery("SELECT c FROM Customer c ORDER BY c.id DESC", Customer.class)
				.getResultList();
	}
}