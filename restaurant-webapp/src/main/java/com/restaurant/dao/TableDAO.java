package com.restaurant.dao;

import com.restaurant.entity.RestaurantTable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TableDAO {
	void insert(RestaurantTable table);
	void update(RestaurantTable table);
	void delete(Long id);
	List<RestaurantTable> findAll();
	List<RestaurantTable> findByAreaAndStatus(String area, String status);
	List<RestaurantTable> findByTime(LocalDate date, String time);
	void updateStatusForTimeSlot(Long tableId, String status);
	Optional<RestaurantTable> findById(Long id);
	Optional<RestaurantTable> findByTableCode(String tableCode);
}
