package com.restaurant.dao.impl;

import com.restaurant.dao.TableDAO;
import com.restaurant.entity.RestaurantTable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public class TableDAOImpl implements TableDAO {
	private static final int DEFAULT_BOOKING_DURATION_HOURS = 2;

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public void insert(RestaurantTable table) {
		entityManager.persist(table);
	}

	@Override
	public void update(RestaurantTable table) {
		entityManager.merge(table);
	}

	@Override
	public void delete(Long id) {
		RestaurantTable table = entityManager.find(RestaurantTable.class, id);
		if (table != null) {
			entityManager.remove(table);
		}
	}

	@Override
	public List<RestaurantTable> findAll() {
		return entityManager.createQuery("SELECT t FROM RestaurantTable t ORDER BY t.id DESC", RestaurantTable.class)
				.getResultList();
	}

	@Override
	public List<RestaurantTable> findByAreaAndStatus(String area, String status) {
		StringBuilder jpql = new StringBuilder("SELECT t FROM RestaurantTable t WHERE 1=1");

		boolean hasArea = area != null && !area.trim().isEmpty();
		boolean hasStatus = status != null && !status.trim().isEmpty();

		if (hasArea) {
			jpql.append(" AND t.area = :area");
		}
		if (hasStatus) {
			jpql.append(" AND t.status = :status");
		}
		jpql.append(" ORDER BY t.id DESC");

		TypedQuery<RestaurantTable> query = entityManager.createQuery(jpql.toString(), RestaurantTable.class);
		if (hasArea) {
			query.setParameter("area", area.trim());
		}
		if (hasStatus) {
			query.setParameter("status", status.trim());
		}

		return query.getResultList();
	}

	@Override
	public List<RestaurantTable> findByTime(LocalDate date, String time) {
		LocalTime requestedStart = parseBookingTime(time);
		LocalTime requestedEnd = requestedStart.plusHours(DEFAULT_BOOKING_DURATION_HOURS);

		return entityManager.createQuery(
						"SELECT t FROM RestaurantTable t " +
					"WHERE t.id NOT IN (" +
				"SELECT st.id FROM BookingOrder b JOIN b.selectedTables st WHERE " +
					"b.bookingDate = :bookingDate " +
					"AND b.status = 'DA_XAC_NHAN' " +
					"AND b.arrivalTime < :requestedEnd " +
					"AND b.endTime > :requestedStart" +
					") " +
						"ORDER BY t.id DESC",
						RestaurantTable.class)
				.setParameter("bookingDate", date)
				.setParameter("requestedStart", requestedStart)
				.setParameter("requestedEnd", requestedEnd)
				.getResultList();
	}

	private LocalTime parseBookingTime(String time) {
		String normalized = time == null ? "" : time.trim();
		if (normalized.length() == 5) {
			normalized = normalized + ":00";
		}
		return LocalTime.parse(normalized);
	}

	@Override
	public void updateStatusForTimeSlot(Long tableId, String status) {
		RestaurantTable table = entityManager.find(RestaurantTable.class, tableId);
		if (table != null) {
			table.setStatus(status);
			entityManager.merge(table);
		}
	}

	@Override
	public Optional<RestaurantTable> findById(Long id) {
		return Optional.ofNullable(entityManager.find(RestaurantTable.class, id));
	}

	@Override
	public Optional<RestaurantTable> findByTableCode(String tableCode) {
		try {
			RestaurantTable table = entityManager.createQuery(
							"SELECT t FROM RestaurantTable t WHERE t.tableCode = :tableCode", RestaurantTable.class)
					.setParameter("tableCode", tableCode)
					.getSingleResult();
			return Optional.of(table);
		} catch (NoResultException ex) {
			return Optional.empty();
		}
	}
}
