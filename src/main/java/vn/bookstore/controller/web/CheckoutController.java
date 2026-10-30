package vn.bookstore.controller.web;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import vn.bookstore.dto.CheckoutRequest;
import vn.bookstore.entity.Cart;
import vn.bookstore.entity.Order;
import vn.bookstore.exception.BusinessException;
import vn.bookstore.security.UserPrincipal;
import vn.bookstore.service.CartService;
import vn.bookstore.service.OrderService;

@Controller
@RequiredArgsConstructor
public class CheckoutController {

	private final CartService cartService;
	private final OrderService orderService;

	@GetMapping("/checkout")
	public String checkoutPage(@AuthenticationPrincipal UserPrincipal principal, Model model) {
		Cart cart = cartService.getOrCreateCart(principal.getUser());
		if (cart.getItems().isEmpty()) {
			return "redirect:/cart";
		}
		model.addAttribute("cart", cart);
		model.addAttribute("checkoutRequest", new CheckoutRequest());
		return "store/checkout";
	}

	@PostMapping("/checkout")
	public String checkout(
			@AuthenticationPrincipal UserPrincipal principal,
			@ModelAttribute CheckoutRequest checkoutRequest,
			RedirectAttributes redirect) {
		try {
			List<Order> orders = orderService.checkout(principal.getUser(), checkoutRequest);
			redirect.addFlashAttribute("alert",
					"Đặt hàng thành công! Đã tạo " + orders.size() + " đơn hàng.");
			return "redirect:/my-orders";
		} catch (BusinessException e) {
			redirect.addFlashAttribute("alert", e.getMessage());
			return "redirect:/checkout";
		}
	}
}
