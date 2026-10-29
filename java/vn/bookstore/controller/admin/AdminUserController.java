package vn.bookstore.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import vn.bookstore.exception.BusinessException;
import vn.bookstore.service.StoreService;
import vn.bookstore.service.UserService;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

	private final UserService userService;
	private final StoreService storeService;

	@GetMapping
	public String list(Model model) {
		model.addAttribute("users", userService.findAll());
		return "admin/user-list";
	}

	@GetMapping("/add")
	public String addForm(Model model) {
		model.addAttribute("stores", storeService.findAll());
		return "admin/user-form";
	}

	@PostMapping("/save")
	public String save(
			@RequestParam String username,
			@RequestParam String password,
			@RequestParam String email,
			@RequestParam String fullName,
			@RequestParam String roleName,
			@RequestParam(required = false) Integer storeId,
			RedirectAttributes redirect) {
		try {
			userService.createStaffAccount(username, password, email, fullName, roleName, storeId);
			redirect.addFlashAttribute("alert", "Tạo tài khoản thành công!");
		} catch (BusinessException e) {
			redirect.addFlashAttribute("alert", e.getMessage());
		}
		return "redirect:/admin/users";
	}

	@GetMapping("/lock/{id}")
	public String lock(@PathVariable Integer id, RedirectAttributes redirect) {
		userService.lockUser(id);
		redirect.addFlashAttribute("alert", "Đã khóa tài khoản!");
		return "redirect:/admin/users";
	}

	@GetMapping("/unlock/{id}")
	public String unlock(@PathVariable Integer id, RedirectAttributes redirect) {
		userService.unlockUser(id);
		redirect.addFlashAttribute("alert", "Đã mở khóa tài khoản!");
		return "redirect:/admin/users";
	}
}
