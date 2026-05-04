package com.restaurant.controller;

import com.restaurant.dto.DishDTO;
import com.restaurant.facade.DishFacade;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/dishes")
public class DishController {

	@Autowired
	private DishFacade dishFacade;

	@GetMapping
	public String listDishes(@RequestParam(required = false) String keyword,
							 @RequestParam(required = false) String category,
							 HttpSession session,
							 Model model) {
		if (!isManager(session)) {
			return "redirect:/login";
		}

		model.addAttribute("dishes", dishFacade.getDishes(keyword, category));
		model.addAttribute("keyword", keyword);
		model.addAttribute("category", category);
		return "dish-list";
	}

	@GetMapping("/new")
	public String newDish(HttpSession session, Model model) {
		if (!isManager(session)) {
			return "redirect:/login";
		}

		model.addAttribute("dishDTO", new DishDTO());
		return "dish-form";
	}

	@GetMapping("/edit/{id}")
	public String editDish(@PathVariable Long id, HttpSession session, Model model) {
		if (!isManager(session)) {
			return "redirect:/login";
		}

		model.addAttribute("dishDTO", dishFacade.getDishForm(id));
		return "dish-form";
	}

	@PostMapping("/save")
	public String saveDish(@Valid @ModelAttribute("dishDTO") DishDTO dishDTO,
						   BindingResult bindingResult,
						   HttpSession session,
						   Model model) {
		if (!isManager(session)) {
			return "redirect:/login";
		}

		if (!dishFacade.isDishCodeUnique(dishDTO.getDishCode(), dishDTO.getId())) {
			model.addAttribute("errorMessage", "Mã món đã tồn tại");
			return "dish-form";
		}

		if (bindingResult.hasErrors()) {
			return "dish-form";
		}

		dishFacade.saveDish(dishDTO);
		return "redirect:/admin/dishes";
	}

	@PostMapping("/delete/{id}")
	public String deleteDish(@PathVariable Long id, HttpSession session) {
		if (!isManager(session)) {
			return "redirect:/login";
		}

		dishFacade.deleteDish(id);
		return "redirect:/admin/dishes";
	}

	@PostMapping("/{id}/status")
	public String updateDishStatus(@PathVariable Long id,
								   @RequestParam("isAvailable") boolean isAvailable,
								   HttpSession session) {
		if (!isManager(session)) {
			return "redirect:/login";
		}

		dishFacade.updateStatus(id, isAvailable);
		return "redirect:/admin/dishes";
	}

	private boolean isManager(HttpSession session) {
		return "MANAGER".equals(session.getAttribute("userRole"));
	}
}
