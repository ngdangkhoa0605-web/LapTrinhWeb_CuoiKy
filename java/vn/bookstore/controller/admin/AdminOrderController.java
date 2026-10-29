package vn.bookstore.controller.admin;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import vn.bookstore.entity.Order;
import vn.bookstore.service.OrderService;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

	private static final int PAGE_SIZE = 10;

	private final OrderService orderService;

	@GetMapping
	public String list(@RequestParam(defaultValue = "0") int page, Model model) {
		Page<Order> orderPage = orderService.findAllForAdmin(page, PAGE_SIZE);
		model.addAttribute("orderPage", orderPage);
		model.addAttribute("orders", orderPage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", orderPage.getTotalPages());
		return "admin/order-list";
	}
}
