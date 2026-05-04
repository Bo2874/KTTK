package com.restaurant.facade;

import com.restaurant.entity.RestaurantTable;
import com.restaurant.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TableFacade {

	@Autowired
	private TableService tableService;

	public List<RestaurantTable> getTables(String area) {
		if (area == null || area.isBlank()) {
			return tableService.findAllTables();
		}
		return tableService.findByAreaAndStatus(area, null);
	}

	public RestaurantTable getById(Long id) {
		return tableService.findById(id).orElse(null);
	}

	public RestaurantTable save(RestaurantTable table) {
		return tableService.saveTable(table);
	}

	public void delete(Long id) {
		tableService.deleteTable(id);
	}

	public boolean isTableCodeUnique(String tableCode, Long expectedId) {
		return tableService.isTableCodeUnique(tableCode, expectedId);
	}
}
