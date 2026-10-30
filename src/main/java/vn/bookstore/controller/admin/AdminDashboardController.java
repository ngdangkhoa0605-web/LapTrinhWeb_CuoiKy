package vn.bookstore.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import vn.bookstore.service.CategoryService;
import vn.bookstore.service.StoreService;
import vn.bookstore.service.UserService;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

	private final StoreService storeService;
	private final CategoryService categoryService;
	private final UserService userService;

	@GetMapping("/dashboard")
	public String dashboard(Model model) {
		model.addAttribute("totalStores", storeService.findAll().size());
		model.addAttribute("totalCategories", categoryService.findAll().size());
		model.addAttribute("totalUsers", userService.findAll().size());
		return "admin/dashboard";
	}
}
