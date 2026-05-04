package com.restaurant.dao.impl;

import com.restaurant.dao.BookingOrderDAO;
import com.restaurant.entity.BookingOrder;
import com.restaurant.entity.Employee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public class BookingOrderDAOImpl implements BookingOrderDAO {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public void insert(BookingOrder bookingOrder) {
		entityManager.persist(bookingOrder);
	}

	@Override
	public BookingOrder update(BookingOrder bookingOrder) {
		return entityManager.merge(bookingOrder);
	}

	@Override
	public void updateBookingStatus(Long bookingOrderId, String status) {
		BookingOrder bookingOrder = entityManager.find(BookingOrder.class, bookingOrderId);
		if (bookingOrder != null) {
			bookingOrder.setStatus(status);
			entityManager.merge(bookingOrder);
		}
	}

	@Override
	public void updateBookingStatusAndEmployee(Long bookingOrderId, String status, Long employeeId) {
		BookingOrder bookingOrder = entityManager.find(BookingOrder.class, bookingOrderId);
		if (bookingOrder == null) {
			return;
		}

		bookingOrder.setStatus(status);
		if (employeeId != null) {
			Employee employee = entityManager.getReference(Employee.class, employeeId);
			bookingOrder.setEmployee(employee);
		}
		entityManager.merge(bookingOrder);
	}

	@Override
	public Optional<BookingOrder> findById(Long id) {
		return Optional.ofNullable(entityManager.find(BookingOrder.class, id));
	}

	@Override
	public List<BookingOrder> findByStatus(String status) {
		return entityManager.createQuery(
					"SELECT b FROM BookingOrder b WHERE b.status = :status ORDER BY b.id DESC",
					BookingOrder.class)
				.setParameter("status", status)
				.getResultList();
	}

	@Override
	public List<BookingOrder> findAll() {
		return entityManager.createQuery(
					"SELECT b FROM BookingOrder b ORDER BY b.id DESC",
					BookingOrder.class)
				.getResultList();
	}

	@Override
	public Optional<BookingOrder> findLatestByCustomerPhone(String phone) {
		List<BookingOrder> result = entityManager.createQuery(
					"SELECT b FROM BookingOrder b JOIN b.customer c " +
					"WHERE c.phone = :phone ORDER BY b.id DESC",
					BookingOrder.class)
				.setParameter("phone", phone)
				.setMaxResults(1)
				.getResultList();

		if (result.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(result.get(0));
	}

	@Override
	public List<BookingOrder> findByCustomerPhone(String phone) {
		return entityManager.createQuery(
					"SELECT b FROM BookingOrder b JOIN b.customer c " +
					"WHERE c.phone = :phone ORDER BY b.id DESC",
					BookingOrder.class)
				.setParameter("phone", phone)
				.getResultList();
	}

	@Override
	public List<BookingOrder> findByCustomerId(Long customerId) {
		return entityManager.createQuery(
					"SELECT b FROM BookingOrder b WHERE b.customer.id = :customerId ORDER BY b.id DESC",
					BookingOrder.class)
				.setParameter("customerId", customerId)
				.getResultList();
	}

	@Override
	public boolean existsConfirmedOverlap(Long tableId,
									 LocalDate bookingDate,
									 LocalTime requestedStart,
									 LocalTime requestedEnd,
									 Long excludeBookingId) {
		if (tableId == null || bookingDate == null || requestedStart == null || requestedEnd == null) {
			return false;
		}

		Long excludedId = excludeBookingId == null ? -1L : excludeBookingId;
		Long overlapCount = entityManager.createQuery(
					"SELECT COUNT(b) FROM BookingOrder b JOIN b.selectedTables st " +
							"WHERE st.id = :tableId " +
							"AND b.bookingDate = :bookingDate " +
							"AND b.status = 'DA_XAC_NHAN' " +
							"AND b.id <> :excludedId " +
							"AND b.arrivalTime < :requestedEnd " +
							"AND b.endTime > :requestedStart",
					Long.class)
				.setParameter("tableId", tableId)
				.setParameter("bookingDate", bookingDate)
				.setParameter("requestedStart", requestedStart)
				.setParameter("requestedEnd", requestedEnd)
				.setParameter("excludedId", excludedId)
				.getSingleResult();

		return overlapCount != null && overlapCount > 0;
	}
}