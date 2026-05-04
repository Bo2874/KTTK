package com.restaurant.dao.impl;

import com.restaurant.dao.DishDAO;
import com.restaurant.entity.Dish;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DishDAOImpl implements DishDAO {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public void insert(Dish dish) {
		entityManager.persist(dish);
	}

	@Override
	public Dish update(Dish dish) {
		return entityManager.merge(dish);
	}

	@Override
	public void delete(Long id) {
		Dish dish = entityManager.find(Dish.class, id);
		if (dish != null) {
			entityManager.remove(dish);
		}
	}

	@Override
	public List<Dish> findAll() {
		return entityManager.createQuery("SELECT d FROM Dish d ORDER BY d.id DESC", Dish.class)
				.getResultList();
	}

	@Override
	public Optional<Dish> findById(Long id) {
		return Optional.ofNullable(entityManager.find(Dish.class, id));
	}

	@Override
	public Optional<Dish> findByDishCode(String dishCode) {
		try {
			Dish dish = entityManager.createQuery(
							"SELECT d FROM Dish d WHERE d.dishCode = :dishCode", Dish.class)
					.setParameter("dishCode", dishCode)
					.getSingleResult();
			return Optional.of(dish);
		} catch (NoResultException ex) {
			return Optional.empty();
		}
	}

	@Override
	public List<Dish> findByKeywordAndCategory(String keyword, String category) {
		StringBuilder jpql = new StringBuilder("SELECT d FROM Dish d WHERE 1=1");

		boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
		boolean hasCategory = category != null && !category.trim().isEmpty();

		if (hasKeyword) {
			jpql.append(" AND (LOWER(d.dishCode) LIKE :keyword OR LOWER(d.name) LIKE :keyword)");
		}
		if (hasCategory) {
			jpql.append(" AND d.category = :category");
		}
		jpql.append(" ORDER BY d.id DESC");

		TypedQuery<Dish> query = entityManager.createQuery(jpql.toString(), Dish.class);
		if (hasKeyword) {
			query.setParameter("keyword", "%" + keyword.trim().toLowerCase() + "%");
		}
		if (hasCategory) {
			query.setParameter("category", category.trim());
		}

		return query.getResultList();
	}
}
