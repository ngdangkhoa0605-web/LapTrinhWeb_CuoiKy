package vn.bookstore.service.impl;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vn.bookstore.entity.Role;
import vn.bookstore.entity.Store;
import vn.bookstore.entity.User;
import vn.bookstore.exception.BusinessException;
import vn.bookstore.repository.RoleRepository;
import vn.bookstore.repository.StoreRepository;
import vn.bookstore.repository.UserRepository;
import vn.bookstore.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final StoreRepository storeRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public User findById(Integer id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new BusinessException("Không tìm thấy người dùng có id = " + id));
	}

	@Override
	public User findByUsername(String username) {
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new BusinessException("Không tìm thấy người dùng: " + username));
	}

	@Override
	public List<User> findAll() {
		return userRepository.findAll();
	}

	@Override
	public List<User> findByRole(String roleName) {
		return userRepository.findAll().stream()
				.filter(u -> u.getRole() != null && u.getRole().getRoleName().equals(roleName))
				.toList();
	}

	@Override
	@Transactional
	public User createStaffAccount(String username, String rawPassword, String email, String fullName,
			String roleName, Integer storeId) {
		if (userRepository.existsByUsername(username)) {
			throw new BusinessException("Tên đăng nhập đã tồn tại!");
		}
		if (userRepository.existsByEmail(email)) {
			throw new BusinessException("Email đã được sử dụng!");
		}

		Role role = roleRepository.findByRoleName(roleName)
				.orElseThrow(() -> new BusinessException("Role không hợp lệ: " + roleName));

		Store store = null;
		if (storeId != null) {
			store = storeRepository.findById(storeId)
					.orElseThrow(() -> new BusinessException("Không tìm thấy chi nhánh id = " + storeId));
		}

		User user = new User();
		user.setUsername(username);
		user.setPassword(passwordEncoder.encode(rawPassword));
		user.setEmail(email);
		user.setFullName(fullName);
		user.setRole(role);
		user.setStore(store);
		user.setStatus(1); // Admin tạo trực tiếp -> kích hoạt luôn, không cần OTP

		return userRepository.save(user);
	}

	@Override
	@Transactional
	public User updateProfile(Integer userId, String fullName, String phone, String address) {
		User user = findById(userId);
		user.setFullName(fullName);
		user.setPhone(phone);
		user.setAddress(address);
		return userRepository.save(user);
	}

	@Override
	@Transactional
	public void lockUser(Integer userId) {
		User user = findById(userId);
		user.setStatus(2);
		userRepository.save(user);
	}

	@Override
	@Transactional
	public void unlockUser(Integer userId) {
		User user = findById(userId);
		user.setStatus(1);
		userRepository.save(user);
	}
}
