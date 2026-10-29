package vn.bookstore.controller.api;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.bookstore.dto.ApiResponse;
import vn.bookstore.dto.JwtResponse;
import vn.bookstore.dto.LoginRequest;
import vn.bookstore.dto.RegisterRequest;
import vn.bookstore.entity.User;
import vn.bookstore.exception.BusinessException;
import vn.bookstore.security.JwtUtil;
import vn.bookstore.security.UserPrincipal;
import vn.bookstore.service.AuthService;

/**
 * REST API xác thực, dùng JWT (stateless). Ví dụ test bằng Postman:
 *
 * POST /api/auth/login
 * Body: { "username": "...", "password": "..." }
 * -> Response trả về "token", đính kèm header
 *    "Authorization: Bearer <token>" cho các API cần xác thực khác.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthApiController {

	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;
	private final AuthService authService;

	@PostMapping("/login")
	public ApiResponse<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
		var authToken = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
		var authentication = authenticationManager.authenticate(authToken);

		UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
		User user = principal.getUser();

		if (!user.isActive()) {
			throw new BusinessException("Tài khoản chưa được kích hoạt. Vui lòng xác thực OTP qua email.");
		}

		String role = user.getRole().getRoleName();
		Integer storeId = user.getStore() != null ? user.getStore().getStoreId() : null;
		String token = jwtUtil.generateToken(user.getUsername(), role, user.getUserId(), storeId);

		JwtResponse response = new JwtResponse(token, user.getUsername(), role, user.getUserId(), storeId);
		return ApiResponse.ok("Đăng nhập thành công", response);
	}

	@PostMapping("/register")
	public ApiResponse<String> register(@Valid @RequestBody RegisterRequest request) {
		User user = authService.register(request);
		return ApiResponse.ok("Đăng ký thành công, vui lòng kiểm tra email để lấy mã OTP kích hoạt",
				user.getUsername());
	}

	@PostMapping("/verify-otp")
	public ApiResponse<String> verifyOtp(@RequestBody java.util.Map<String, String> body) {
		boolean ok = authService.verifyActivationOtp(body.get("username"), body.get("otp"));
		if (!ok) {
			throw new BusinessException("Mã OTP không đúng hoặc đã hết hạn");
		}
		return ApiResponse.ok("Kích hoạt tài khoản thành công", null);
	}
}
