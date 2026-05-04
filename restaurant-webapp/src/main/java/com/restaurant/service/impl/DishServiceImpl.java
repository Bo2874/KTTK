package com.restaurant.service.impl;

import com.restaurant.dao.DishDAO;
import com.restaurant.dto.DishDTO;
import com.restaurant.entity.Dish;
import com.restaurant.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {
	private static final String DEFAULT_DISH_IMAGE_URL = "/images/default-dish.png";

	@Autowired
	private DishDAO dishDAO;

	@Override
	@Transactional(readOnly = true)
	public List<Dish> findDishes(String keyword, String category) {
		return dishDAO.findByKeywordAndCategory(keyword, category);
	}

	@Override
	@Transactional(readOnly = true)
	public DishDTO getDishDTOById(Long id) {
		return dishDAO.findById(id).map(this::toDTO).orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public Dish getDishById(Long id) {
		return dishDAO.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public Dish saveDish(DishDTO dishDTO) {
		Dish dish = dishDTO.getId() == null
				? new Dish()
				: dishDAO.findById(dishDTO.getId()).orElse(new Dish());

		dish.setDishCode(dishDTO.getDishCode());
		dish.setName(dishDTO.getName());
		dish.setCategory(dishDTO.getCategory());
		dish.setPrice(dishDTO.getPrice());
		dish.setDescription(normalizeDescription(dishDTO.getDescription()));
		dish.setIsAvailable(dishDTO.getIsAvailable() != null ? dishDTO.getIsAvailable() : Boolean.TRUE);

		if (dish.getImageUrl() == null || dish.getImageUrl().isBlank()) {
			dish.setImageUrl(DEFAULT_DISH_IMAGE_URL);
		}

		if (dish.getId() == null) {
			dishDAO.insert(dish);
			return dish;
		}
		return dishDAO.update(dish);
	}

	@Override
	@Transactional
	public void deleteDish(Long id) {
		dishDAO.delete(id);
	}

	@Override
	@Transactional
	public void updateDishStatus(Long id, boolean isAvailable) {
		dishDAO.findById(id).ifPresent(dish -> {
			dish.setIsAvailable(isAvailable);
			dishDAO.update(dish);
		});
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isDishCodeUnique(String dishCode, Long expectedId) {
		return dishDAO.findByDishCode(dishCode)
				.map(existing -> existing.getId().equals(expectedId))
				.orElse(true);
	}

	private String normalizeDescription(String description) {
		if (description == null || description.isBlank()) {
			return "Chưa có mô tả";
		}
		return description.trim();
	}

	private DishDTO toDTO(Dish dish) {
		DishDTO dto = new DishDTO();
		dto.setId(dish.getId());
		dto.setDishCode(dish.getDishCode());
		dto.setName(dish.getName());
		dto.setCategory(dish.getCategory());
		dto.setPrice(dish.getPrice());
		dto.setDescription(dish.getDescription());
		dto.setIsAvailable(dish.getIsAvailable());
		return dto;
	}
}
