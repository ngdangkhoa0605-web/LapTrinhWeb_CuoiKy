package vn.bookstore.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Sau khi đăng nhập (form login - Session) THÀNH CÔNG, điều hướng theo vai
 * trò: ADMIN -> trang quản trị hệ thống, MANAGER/STAFF -> dashboard chi
 * nhánh, CUSTOMER -> trang chủ.
 */
@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		String role = authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.findFirst()
				.orElse("");

		String targetUrl;
		switch (role) {
			case "ROLE_ADMIN":
				targetUrl = "/admin/dashboard";
				break;
			case "ROLE_MANAGER":
			case "ROLE_STAFF":
				targetUrl = "/manager/dashboard";
				break;
			default:
				targetUrl = "/home";
		}

		getRedirectStrategy().sendRedirect(request, response, request.getContextPath() + targetUrl);
	}
}
