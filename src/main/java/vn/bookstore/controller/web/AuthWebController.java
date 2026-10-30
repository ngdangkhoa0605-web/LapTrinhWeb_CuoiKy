package vn.bookstore.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import vn.bookstore.dto.RegisterRequest;
import vn.bookstore.exception.BusinessException;
import vn.bookstore.service.AuthService;

/**
 * Toàn bộ luồng này dùng SESSION (không dùng JWT) - đúng theo lựa chọn kiến
 * trúc: JSP dùng Spring Security session-based, JWT chỉ dành cho "/api/**".
 * Việc đăng nhập (POST /login) do Spring Security's UsernamePasswordAuthenticationFilter
 * xử lý tự động (xem SecurityConfig), Controller này CHỈ hiển thị trang login,
 * không tự xử lý logic xác thực.
 */
@Controller
@RequiredArgsConstructor
public class AuthWebController {

	private final AuthService authService;

	@GetMapping("/login")
	public String loginPage() {
		return "auth/login";
	}

	@GetMapping("/register")
	public String registerPage() {
		return "auth/register";
	}

	@PostMapping("/register")
	public String register(RegisterRequest request, RedirectAttributes redirect) {
		try {
			authService.register(request);
			return "redirect:/verify-otp?username=" + request.getUsername();
		} catch (BusinessException e) {
			redirect.addFlashAttribute("alert", e.getMessage());
			return "redirect:/register";
		}
	}

	@GetMapping("/verify-otp")
	public String verifyOtpPage(
			@RequestParam String username,
			@RequestParam(required = false) String resend,
			Model model) {
		if ("1".equals(resend)) {
			boolean sent = authService.sendActivationOtp(username);
			model.addAttribute("alert",
					sent ? "Đã gửi lại mã OTP, vui lòng kiểm tra email!" : "Gửi email thất bại, vui lòng thử lại!");
		}
		model.addAttribute("username", username);
		return "auth/verify-otp";
	}

	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam String username, @RequestParam String otp, RedirectAttributes redirect) {
		boolean ok = authService.verifyActivationOtp(username, otp.trim());
		if (ok) {
			return "redirect:/login?activated=1";
		}
		redirect.addFlashAttribute("alert", "Mã OTP không đúng hoặc đã hết hạn!");
		return "redirect:/verify-otp?username=" + username;
	}

	@GetMapping("/forgot-password")
	public String forgotPasswordPage() {
		return "auth/forgot-password";
	}

	@PostMapping("/forgot-password")
	public String forgotPassword(@RequestParam String email, RedirectAttributes redirect) {
		boolean sent = authService.sendForgotPasswordOtp(email.trim());
		if (sent) {
			return "redirect:/reset-password?email=" + email.trim();
		}
		redirect.addFlashAttribute("alert", "Email không tồn tại trong hệ thống!");
		return "redirect:/forgot-password";
	}

	@GetMapping("/reset-password")
	public String resetPasswordPage(
			@RequestParam String email,
			@RequestParam(required = false) String resend,
			Model model) {
		if ("1".equals(resend)) {
			boolean sent = authService.sendForgotPasswordOtp(email);
			model.addAttribute("alert",
					sent ? "Đã gửi lại mã OTP, vui lòng kiểm tra email!" : "Gửi email thất bại, vui lòng thử lại!");
		}
		model.addAttribute("email", email);
		return "auth/reset-password";
	}

	@PostMapping("/reset-password")
	public String resetPassword(
			@RequestParam String email,
			@RequestParam String otp,
			@RequestParam String newPassword,
			@RequestParam String renewPassword,
			RedirectAttributes redirect) {

		if (!newPassword.equals(renewPassword)) {
			redirect.addFlashAttribute("alert", "Mật khẩu nhập lại không khớp!");
			return "redirect:/reset-password?email=" + email;
		}

		boolean ok = authService.resetPassword(email, otp.trim(), newPassword);
		if (ok) {
			return "redirect:/login?resetok=1";
		}
		redirect.addFlashAttribute("alert", "Mã OTP không đúng hoặc đã hết hạn!");
		return "redirect:/reset-password?email=" + email;
	}
}
