package com.restaurant.controller;

import com.restaurant.entity.RestaurantTable;
import com.restaurant.facade.TableFacade;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/tables")
public class TableController {

	@Autowired
	private TableFacade tableFacade;

	@GetMapping
	public String listTables(@RequestParam(required = false) String area,
							 HttpSession session,
							 Model model) {
		if (!isManager(session)) {
			return "redirect:/login";
		}

		model.addAttribute("tables", tableFacade.getTables(area));
		return "table-grid";
	}

	@GetMapping("/add")
	public String showAddForm(HttpSession session, Model model) {
		if (!isManager(session)) {
			return "redirect:/login";
		}

		RestaurantTable table = new RestaurantTable();
		table.setStatus("AVAILABLE");
		model.addAttribute("table", table);
		return "table-form";
	}

	@GetMapping("/edit/{id}")
	public String showEditForm(@PathVariable Long id, HttpSession session, Model model) {
		if (!isManager(session)) {
			return "redirect:/login";
		}

		RestaurantTable table = tableFacade.getById(id);
		if (table == null) {
			return "redirect:/admin/tables";
		}

		model.addAttribute("table", table);
		return "table-form";
	}

	@PostMapping("/save")
	public String saveTable(@ModelAttribute("table") RestaurantTable table,
							HttpSession session,
							Model model) {
		if (!isManager(session)) {
			return "redirect:/login";
		}

		if (!tableFacade.isTableCodeUnique(table.getTableCode(), table.getId())) {
			model.addAttribute("errorMessage", "Mã bàn đã tồn tại");
			model.addAttribute("table", table);
			return "table-form";
		}

		// Manager module no longer edits runtime status manually.
		if (table.getId() == null) {
			table.setStatus("AVAILABLE");
		} else {
			RestaurantTable existing = tableFacade.getById(table.getId());
			if (existing != null) {
				table.setStatus(existing.getStatus());
			}
		}

		tableFacade.save(table);
		return "redirect:/admin/tables";
	}

	@PostMapping("/delete/{id}")
	public String deleteTable(@PathVariable Long id, HttpSession session) {
		if (!isManager(session)) {
			return "redirect:/login";
		}

		tableFacade.delete(id);
		return "redirect:/admin/tables";
	}

	private boolean isManager(HttpSession session) {
		return "MANAGER".equals(session.getAttribute("userRole"));
	}
}
