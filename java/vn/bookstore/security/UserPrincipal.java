package vn.bookstore.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import vn.bookstore.entity.User;

/**
 * Bọc entity User thành UserDetails để Spring Security (cả Session lẫn JWT
 * filter) dùng chung 1 cơ chế xác thực/phân quyền.
 */
public class UserPrincipal implements UserDetails {
	private static final long serialVersionUID = 1L;

	private final User user;

	public UserPrincipal(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public Integer getUserId() {
		return user.getUserId();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// roleName trong DB đã có dạng "ROLE_ADMIN", "ROLE_MANAGER", ...
		return List.of(new SimpleGrantedAuthority(user.getRole().getRoleName()));
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return user.getStatus() != 2; // 2 = bị khóa
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return user.getStatus() == 1; // chỉ tài khoản đã kích hoạt OTP mới được coi là "enabled"
	}
}
