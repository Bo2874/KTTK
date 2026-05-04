package com.restaurant.service.impl;

import com.restaurant.dao.TableDAO;
import com.restaurant.entity.RestaurantTable;
import com.restaurant.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TableServiceImpl implements TableService {

	@Autowired
	private TableDAO tableDAO;

	@Override
	@Transactional(readOnly = true)
	public List<RestaurantTable> findAllTables() {
		return tableDAO.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public List<RestaurantTable> findByAreaAndStatus(String area, String status) {
		return tableDAO.findByAreaAndStatus(area, status);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<RestaurantTable> findById(Long id) {
		return tableDAO.findById(id);
	}

	@Override
	@Transactional
	public void deleteTable(Long id) {
		tableDAO.delete(id);
	}

	@Override
	@Transactional
	public void updateStatus(Long id, String status) {
		tableDAO.findById(id).ifPresent(table -> {
			table.setStatus(status);
			tableDAO.update(table);
		});
	}

	@Override
	@Transactional
	public void updateTableStatus(Long tableId, String status) {
		tableDAO.updateStatusForTimeSlot(tableId, status);
	}

	@Override
	@Transactional
	public RestaurantTable saveTable(RestaurantTable table) {
		if (table.getId() == null) {
			if (table.getStatus() == null || table.getStatus().isBlank()) {
				table.setStatus("AVAILABLE");
			}
			tableDAO.insert(table);
			return table;
		}

		if (table.getStatus() == null || table.getStatus().isBlank()) {
			tableDAO.findById(table.getId())
					.ifPresent(existing -> table.setStatus(existing.getStatus()));
		}

		tableDAO.update(table);
		return table;
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isTableCodeUnique(String tableCode, Long expectedId) {
		return tableDAO.findByTableCode(tableCode)
				.map(existing -> existing.getId().equals(expectedId))
				.orElse(true);
	}

	@Override
	@Transactional(readOnly = true)
	public List<RestaurantTable> getAvailableTables(String date, String time) {
		try {
			LocalDate bookingDate = LocalDate.parse(date);
			return tableDAO.findByTime(bookingDate, time);
		} catch (Exception ex) {
			return List.of();
		}
	}
}
