package vn.bookstore.controller.admin;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import vn.bookstore.entity.Category;
import vn.bookstore.exception.BusinessException;
import vn.bookstore.service.CategoryService;
import vn.bookstore.service.CloudinaryService;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

	private final CategoryService categoryService;
	private final CloudinaryService cloudinaryService;

	@GetMapping
	public String list(@RequestParam(required = false) String keyword, Model model) {
		model.addAttribute("categories",
				(keyword != null && !keyword.isBlank()) ? categoryService.search(keyword) : categoryService.findAll());
		model.addAttribute("keyword", keyword);
		return "admin/category-list";
	}

	@GetMapping("/add")
	public String addForm(Model model) {
		model.addAttribute("category", new Category());
		return "admin/category-form";
	}

	@PostMapping("/save")
	public String save(
			@ModelAttribute Category category,
			@RequestParam(required = false) MultipartFile imageFile,
			RedirectAttributes redirect) {
		try {
			if (category.getCategoryId() != null) {
				Category existing = categoryService.findById(category.getCategoryId());
				existing.setCategoryName(category.getCategoryName());
				existing.setDescription(category.getDescription());
				existing.setStatus(category.getStatus());
				if (imageFile != null && !imageFile.isEmpty()) {
					existing.setImage(cloudinaryService.uploadImage(imageFile, "categories"));
				}
				categoryService.save(existing);
			} else {
				if (imageFile != null && !imageFile.isEmpty()) {
					category.setImage(cloudinaryService.uploadImage(imageFile, "categories"));
				}
				categoryService.save(category);
			}
			redirect.addFlashAttribute("alert", "Lưu danh mục thành công!");
		} catch (BusinessException | IOException e) {
			redirect.addFlashAttribute("alert", "Lỗi: " + e.getMessage());
		}
		return "redirect:/admin/categories";
	}

	@GetMapping("/edit/{id}")
	public String editForm(@PathVariable Integer id, Model model) {
		model.addAttribute("category", categoryService.findById(id));
		return "admin/category-form";
	}

	@GetMapping("/delete/{id}")
	public String delete(@PathVariable Integer id, RedirectAttributes redirect) {
		try {
			categoryService.delete(id);
			redirect.addFlashAttribute("alert", "Đã xóa danh mục!");
		} catch (BusinessException e) {
			redirect.addFlashAttribute("alert", e.getMessage());
		}
		return "redirect:/admin/categories";
	}
}
