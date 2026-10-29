package vn.bookstore.controller.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import lombok.RequiredArgsConstructor;
import vn.bookstore.entity.Order;
import vn.bookstore.security.UserPrincipal;
import vn.bookstore.service.OrderService;

@Controller
@RequiredArgsConstructor
public class MyOrdersController {

	private final OrderService orderService;

	@GetMapping("/my-orders")
	public String myOrders(@AuthenticationPrincipal UserPrincipal principal, Model model) {
		model.addAttribute("orders", orderService.findByCustomer(principal.getUserId()));
		return "store/my-orders";
	}

	@GetMapping("/my-orders/{id}")
	public String orderDetail(@PathVariable Integer id, @AuthenticationPrincipal UserPrincipal principal,
			Model model) {
		Order order = orderService.findById(id);
		if (!order.getCustomer().getUserId().equals(principal.getUserId())) {
			return "redirect:/my-orders";
		}
		model.addAttribute("order", order);
		return "store/order-detail";
	}
}
