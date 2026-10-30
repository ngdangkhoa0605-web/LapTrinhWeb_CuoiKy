package vn.bookstore.controller.manager;

import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import vn.bookstore.entity.Order;
import vn.bookstore.entity.Store;
import vn.bookstore.exception.BusinessException;
import vn.bookstore.security.UserPrincipal;
import vn.bookstore.service.OrderService;

/** Manager/Staff quản lý đơn hàng CỦA CHI NHÁNH MÌNH - cập nhật trạng thái xử lý đơn. */
@Controller
@RequestMapping("/manager/orders")
@RequiredArgsConstructor
public class ManagerOrderController {

	private static final int PAGE_SIZE = 10;

	private final OrderService orderService;

	@GetMapping
	public String list(
			@AuthenticationPrincipal UserPrincipal principal,
			@RequestParam(required = false) String status,
			@RequestParam(defaultValue = "0") int page,
			Model model) {
		Store store = requireStore(principal);
		Page<Order> orderPage = orderService.findByStore(store.getStoreId(), status, page, PAGE_SIZE);
		model.addAttribute("orderPage", orderPage);
		model.addAttribute("orders", orderPage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", orderPage.getTotalPages());
		model.addAttribute("status", status);
		return "manager/order-list";
	}

	@GetMapping("/{id}")
	public String detail(@PathVariable Integer id, @AuthenticationPrincipal UserPrincipal principal, Model model) {
		Order order = orderService.findById(id);
		checkOwnership(order, principal);
		model.addAttribute("order", order);
		return "manager/order-detail";
	}

	@PostMapping("/{id}/status")
	public String updateStatus(
			@PathVariable Integer id,
			@RequestParam String status,
			@AuthenticationPrincipal UserPrincipal principal,
			RedirectAttributes redirect) {
		Order order = orderService.findById(id);
		checkOwnership(order, principal);
		orderService.updateStatus(id, status);
		redirect.addFlashAttribute("alert", "Đã cập nhật trạng thái đơn hàng!");
		return "redirect:/manager/orders/" + id;
	}

	private Store requireStore(UserPrincipal principal) {
		Store store = principal.getUser().getStore();
		if (store == null) {
			throw new BusinessException("Tài khoản chưa được gán vào chi nhánh nào!");
		}
		return store;
	}

	private void checkOwnership(Order order, UserPrincipal principal) {
		Store myStore = requireStore(principal);
		boolean isAdmin = principal.getAuthorities().stream()
				.anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
		if (!isAdmin && !order.getStore().getStoreId().equals(myStore.getStoreId())) {
			throw new BusinessException("Bạn không có quyền xem đơn hàng của chi nhánh khác!");
		}
	}
}
