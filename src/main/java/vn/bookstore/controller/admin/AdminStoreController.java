package vn.bookstore.controller.admin;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import vn.bookstore.entity.Store;
import vn.bookstore.exception.BusinessException;
import vn.bookstore.service.CloudinaryService;
import vn.bookstore.service.StoreService;

@Controller
@RequestMapping("/admin/stores")
@RequiredArgsConstructor
public class AdminStoreController {

	private final StoreService storeService;
	private final CloudinaryService cloudinaryService;

	@GetMapping
	public String list(Model model) {
		model.addAttribute("stores", storeService.findAll());
		return "admin/store-list";
	}

	@GetMapping("/add")
	public String addForm(Model model) {
		model.addAttribute("store", new Store());
		return "admin/store-form";
	}

	@PostMapping("/save")
	public String save(
			@ModelAttribute Store store,
			@RequestParam(required = false) MultipartFile imageFile,
			RedirectAttributes redirect) {
		try {
			if (store.getStoreId() != null) {
				Store existing = storeService.findById(store.getStoreId());
				existing.setStoreName(store.getStoreName());
				existing.setAddress(store.getAddress());
				existing.setPhone(store.getPhone());
				existing.setStatus(store.getStatus());
				if (imageFile != null && !imageFile.isEmpty()) {
					existing.setImage(cloudinaryService.uploadImage(imageFile, "stores"));
				}
				storeService.save(existing);
			} else {
				if (imageFile != null && !imageFile.isEmpty()) {
					store.setImage(cloudinaryService.uploadImage(imageFile, "stores"));
				}
				storeService.save(store);
			}
			redirect.addFlashAttribute("alert", "Lưu chi nhánh thành công!");
		} catch (BusinessException | IOException e) {
			redirect.addFlashAttribute("alert", "Lỗi: " + e.getMessage());
		}
		return "redirect:/admin/stores";
	}

	@GetMapping("/edit/{id}")
	public String editForm(@PathVariable Integer id, Model model) {
		model.addAttribute("store", storeService.findById(id));
		return "admin/store-form";
	}

	@GetMapping("/delete/{id}")
	public String delete(@PathVariable Integer id, RedirectAttributes redirect) {
		try {
			storeService.delete(id);
			redirect.addFlashAttribute("alert", "Đã xóa chi nhánh!");
		} catch (BusinessException e) {
			redirect.addFlashAttribute("alert", e.getMessage());
		}
		return "redirect:/admin/stores";
	}
}
