package vn.bookstore.security;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Khi đăng nhập (form login - Session) THẤT BẠI, giữ lại username đã nhập
 * để login.jsp có thể hiển thị link "Nhập OTP kích hoạt" đúng tài khoản
 * (trường hợp lỗi là do tài khoản chưa kích hoạt - DisabledException).
 */
@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {

		String username = request.getParameter("username");
		String encodedUsername = username != null
				? URLEncoder.encode(username, StandardCharsets.UTF_8)
				: "";

		String targetUrl = request.getContextPath() + "/login?error=1&username=" + encodedUsername;
		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}
}
