package com.restaurant.facade;

import com.restaurant.dto.DishDTO;
import com.restaurant.entity.Dish;
import com.restaurant.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DishFacade {

	@Autowired
	private DishService dishService;

	public List<Dish> getDishes(String keyword, String category) {
		return dishService.findDishes(keyword, category);
	}

	public DishDTO getDishForm(Long id) {
		if (id == null) {
			return new DishDTO();
		}
		DishDTO dto = dishService.getDishDTOById(id);
		return dto != null ? dto : new DishDTO();
	}

	public Dish saveDish(DishDTO dishDTO) {
		return dishService.saveDish(dishDTO);
	}

	public void deleteDish(Long id) {
		dishService.deleteDish(id);
	}

	public void updateStatus(Long id, boolean isAvailable) {
		dishService.updateDishStatus(id, isAvailable);
	}

	public boolean isDishCodeUnique(String dishCode, Long expectedId) {
		return dishService.isDishCodeUnique(dishCode, expectedId);
	}
}
