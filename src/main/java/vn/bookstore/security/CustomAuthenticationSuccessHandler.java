package vn.bookstore.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationSuccessHandler
        implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {

        /*
         * Không redirect trực tiếp vào /admin hoặc /manager.
         *
         * Sau khi login thành công:
         *     /login POST
         *          ↓
         *       /home
         *
         * /home là PUBLIC nên không thể tạo vòng
         * /login -> /admin -> /login.
         */

        String contextPath = request.getContextPath();

        if (contextPath == null) {
            contextPath = "";
        }

        response.sendRedirect(
                contextPath + "/home"
        );
    }
}
