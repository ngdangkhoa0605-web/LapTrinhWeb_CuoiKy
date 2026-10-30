package vn.bookstore.service.impl;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vn.bookstore.dto.RegisterRequest;
import vn.bookstore.entity.Role;
import vn.bookstore.entity.User;
import vn.bookstore.exception.BusinessException;
import vn.bookstore.repository.RoleRepository;
import vn.bookstore.repository.UserRepository;
import vn.bookstore.service.AuthService;
import vn.bookstore.service.MailService;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final MailService mailService;

	@Value("${app.otp.expire-minutes}")
	private int otpExpireMinutes;

	private static final SecureRandom RANDOM = new SecureRandom();

	@Override
	@Transactional
	public User register(RegisterRequest request) {
		if (userRepository.existsByUsername(request.getUsername())) {
			throw new BusinessException("Tên đăng nhập đã tồn tại!");
		}
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new BusinessException("Email đã được sử dụng!");
		}

		Role customerRole = roleRepository.findByRoleName("ROLE_CUSTOMER")
				.orElseThrow(() -> new BusinessException("Chưa cấu hình Role CUSTOMER trong hệ thống"));

		User user = new User();
		user.setUsername(request.getUsername());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setEmail(request.getEmail());
		user.setFullName(request.getFullName());
		user.setPhone(request.getPhone());
		user.setRole(customerRole);
		user.setStatus(0); // chưa kích hoạt

		user = userRepository.save(user);

		sendActivationOtp(user.getUsername());
		return user;
	}

	@Override
	@Transactional
	public boolean sendActivationOtp(String username) {
		User user = userRepository.findByUsername(username).orElse(null);
		if (user == null) {
			return false;
		}
		String otp = generateOtp();
		user.setOtp(otp);
		user.setOtpExpire(LocalDateTime.now().plusMinutes(otpExpireMinutes));
		userRepository.save(user);
		return mailService.sendOtpMail(user.getEmail(), otp, "REGISTER");
	}

	@Override
	@Transactional
	public boolean verifyActivationOtp(String username, String otp) {
		User user = userRepository.findByUsername(username).orElse(null);
		if (user == null || user.getOtp() == null) {
			return false;
		}
		if (!user.getOtp().equals(otp)) {
			return false;
		}
		if (user.getOtpExpire() == null || user.getOtpExpire().isBefore(LocalDateTime.now())) {
			return false;
		}
		user.setStatus(1);
		user.setOtp(null);
		user.setOtpExpire(null);
		userRepository.save(user);
		return true;
	}

	@Override
	@Transactional
	public boolean sendForgotPasswordOtp(String email) {
		User user = userRepository.findByEmail(email).orElse(null);
		if (user == null) {
			return false;
		}
		String otp = generateOtp();
		user.setOtp(otp);
		user.setOtpExpire(LocalDateTime.now().plusMinutes(otpExpireMinutes));
		userRepository.save(user);
		return mailService.sendOtpMail(user.getEmail(), otp, "FORGOT_PASSWORD");
	}

	@Override
	@Transactional
	public boolean resetPassword(String email, String otp, String newPassword) {
		User user = userRepository.findByEmail(email).orElse(null);
		if (user == null || user.getOtp() == null) {
			return false;
		}
		if (!user.getOtp().equals(otp)) {
			return false;
		}
		if (user.getOtpExpire() == null || user.getOtpExpire().isBefore(LocalDateTime.now())) {
			return false;
		}
		user.setPassword(passwordEncoder.encode(newPassword));
		user.setOtp(null);
		user.setOtpExpire(null);
		userRepository.save(user);
		return true;
	}

	private String generateOtp() {
		int number = RANDOM.nextInt(1_000_000);
		return String.format("%06d", number);
	}
}
