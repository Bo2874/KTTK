package com.restaurant.service;

import com.restaurant.dto.DishDTO;
import com.restaurant.entity.Dish;

import java.util.List;

public interface DishService {
	List<Dish> findDishes(String keyword, String category);
	DishDTO getDishDTOById(Long id);
	Dish getDishById(Long id);
	Dish saveDish(DishDTO dishDTO);
	void deleteDish(Long id);
	void updateDishStatus(Long id, boolean isAvailable);
	boolean isDishCodeUnique(String dishCode, Long expectedId);
}
