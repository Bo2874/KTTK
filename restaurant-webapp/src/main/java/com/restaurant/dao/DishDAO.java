package com.restaurant.dao;

import com.restaurant.entity.Dish;

import java.util.List;
import java.util.Optional;

public interface DishDAO {
	void insert(Dish dish);
	Dish update(Dish dish);
	void delete(Long id);
	List<Dish> findAll();
	Optional<Dish> findById(Long id);
	Optional<Dish> findByDishCode(String dishCode);
	List<Dish> findByKeywordAndCategory(String keyword, String category);
}
