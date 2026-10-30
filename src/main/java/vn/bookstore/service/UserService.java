package vn.bookstore.service;

import java.util.List;

import vn.bookstore.entity.User;

public interface UserService {
	User findById(Integer id);
	User findByUsername(String username);
	List<User> findAll();
	List<User> findByRole(String roleName);

	/** Admin tạo tài khoản Nhân viên/Quản lý chi nhánh (status = 1, kích hoạt luôn, không cần OTP). */
	User createStaffAccount(String username, String rawPassword, String email, String fullName,
			String roleName, Integer storeId);

	User updateProfile(Integer userId, String fullName, String phone, String address);
	void lockUser(Integer userId);
	void unlockUser(Integer userId);
}
