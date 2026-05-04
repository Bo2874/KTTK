package com.restaurant.dao.impl;

import com.restaurant.dao.OrderDetailDAO;
import com.restaurant.entity.OrderDetail;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderDetailDAOImpl implements OrderDetailDAO {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public void insert(OrderDetail orderDetail) {
		entityManager.persist(orderDetail);
	}

	@Override
	public void insertAll(List<OrderDetail> orderDetails) {
		for (OrderDetail detail : orderDetails) {
			entityManager.persist(detail);
		}
	}

	@Override
	public List<OrderDetail> findByBookingOrderId(Long bookingOrderId) {
		return entityManager.createQuery(
					"SELECT od FROM OrderDetail od WHERE od.bookingOrder.id = :bookingOrderId ORDER BY od.id ASC",
					OrderDetail.class)
				.setParameter("bookingOrderId", bookingOrderId)
				.getResultList();
	}

	@Override
	public void deleteByBookingOrderId(Long bookingOrderId) {
		entityManager.createQuery(
					"DELETE FROM OrderDetail od WHERE od.bookingOrder.id = :bookingOrderId")
				.setParameter("bookingOrderId", bookingOrderId)
				.executeUpdate();
	}
}