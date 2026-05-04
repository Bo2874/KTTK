package com.restaurant.dao.impl;

import com.restaurant.dao.EmployeeDAO;
import com.restaurant.entity.Employee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class EmployeeDAOImpl implements EmployeeDAO {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Optional<Employee> findById(Long id) {
		return Optional.ofNullable(entityManager.find(Employee.class, id));
	}

	@Override
	public Optional<Employee> findByEmployeeCode(String employeeCode) {
		try {
			Employee employee = entityManager.createQuery(
						"SELECT e FROM Employee e WHERE e.employeeCode = :employeeCode", Employee.class)
					.setParameter("employeeCode", employeeCode)
					.getSingleResult();
			return Optional.of(employee);
		} catch (NoResultException ex) {
			return Optional.empty();
		}
	}

	@Override
	public Optional<Employee> findByUsername(String username) {
		try {
			Employee employee = entityManager.createQuery(
							"SELECT e FROM Employee e WHERE e.username = :username", Employee.class)
					.setParameter("username", username)
					.getSingleResult();
			return Optional.of(employee);
		} catch (NoResultException ex) {
			return Optional.empty();
		}
	}

	@Override
	public Employee save(Employee employee) {
		if (employee.getId() == null) {
			entityManager.persist(employee);
			return employee;
		}
		return entityManager.merge(employee);
	}

	@Override
	public List<Employee> findAll() {
		return entityManager.createQuery("SELECT e FROM Employee e ORDER BY e.id DESC", Employee.class)
				.getResultList();
	}
}
