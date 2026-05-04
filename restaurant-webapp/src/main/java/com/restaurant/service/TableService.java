package com.restaurant.service;

import com.restaurant.entity.RestaurantTable;

import java.util.List;
import java.util.Optional;

public interface TableService {
	List<RestaurantTable> findAllTables();
	List<RestaurantTable> findByAreaAndStatus(String area, String status);
	Optional<RestaurantTable> findById(Long id);
	void deleteTable(Long id);
	void updateStatus(Long id, String status);
	void updateTableStatus(Long tableId, String status);
	RestaurantTable saveTable(RestaurantTable table);
	boolean isTableCodeUnique(String tableCode, Long expectedId);
	List<RestaurantTable> getAvailableTables(String date, String time);
}
