package vn.bookstore.controller.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import vn.bookstore.entity.Cart;
import vn.bookstore.exception.BusinessException;
import vn.bookstore.security.UserPrincipal;
import vn.bookstore.service.CartService;

/** Giỏ hàng - chỉ CUSTOMER đã đăng nhập mới truy cập được (xem SecurityConfig). */
@Controller
@RequiredArgsConstructor
public class CartController {

	private final CartService cartService;

	@GetMapping("/cart")
	public String viewCart(@AuthenticationPrincipal UserPrincipal principal, Model model) {
		Cart cart = cartService.getOrCreateCart(principal.getUser());
		model.addAttribute("cart", cart);
		return "store/cart";
	}

	@PostMapping("/cart/add")
	public String addToCart(
			@AuthenticationPrincipal UserPrincipal principal,
			@RequestParam Integer bookId,
			@RequestParam(defaultValue = "1") int quantity,
			RedirectAttributes redirect) {
		try {
			cartService.addItem(principal.getUser(), bookId, quantity);
			redirect.addFlashAttribute("alert", "Đã thêm vào giỏ hàng!");
		} catch (BusinessException e) {
			redirect.addFlashAttribute("alert", e.getMessage());
		}
		return "redirect:/books/" + bookId;
	}

	@PostMapping("/cart/update/{cartItemId}")
	public String updateQuantity(
			@AuthenticationPrincipal UserPrincipal principal,
			@PathVariable Integer cartItemId,
			@RequestParam int quantity,
			RedirectAttributes redirect) {
		try {
			cartService.updateItemQuantity(principal.getUser(), cartItemId, quantity);
		} catch (BusinessException e) {
			redirect.addFlashAttribute("alert", e.getMessage());
		}
		return "redirect:/cart";
	}

	@PostMapping("/cart/remove/{cartItemId}")
	public String removeItem(
			@AuthenticationPrincipal UserPrincipal principal,
			@PathVariable Integer cartItemId) {
		cartService.removeItem(principal.getUser(), cartItemId);
		return "redirect:/cart";
	}
}
