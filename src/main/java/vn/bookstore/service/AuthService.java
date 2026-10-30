package vn.bookstore.service;

import vn.bookstore.dto.RegisterRequest;
import vn.bookstore.entity.User;

public interface AuthService {
	/** Đăng ký khách hàng mới (role CUSTOMER, status=0 chưa kích hoạt) + gửi OTP. */
	User register(RegisterRequest request);

	boolean sendActivationOtp(String username);
	boolean verifyActivationOtp(String username, String otp);

	boolean sendForgotPasswordOtp(String email);
	boolean resetPassword(String email, String otp, String newPassword);
}
