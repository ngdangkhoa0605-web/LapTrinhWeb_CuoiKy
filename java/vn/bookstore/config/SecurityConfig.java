package vn.bookstore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import lombok.RequiredArgsConstructor;
import vn.bookstore.security.CustomAuthenticationFailureHandler;
import vn.bookstore.security.CustomAuthenticationSuccessHandler;
import vn.bookstore.security.JwtAuthFilter;

/**
 * Hệ thống có 2 cơ chế xác thực SONG SONG, tách biệt hoàn toàn bằng 2
 * SecurityFilterChain khác nhau (phân biệt qua tiền tố URL):
 *
 * 1) "/api/**"  -> STATELESS, xác thực bằng JWT (JwtAuthFilter).
 * 2) Phần còn lại (toàn bộ trang JSP) -> Session-based, dùng Spring
 *    Security form login truyền thống (login.jsp).
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final UserDetailsService userDetailsService;
	private final JwtAuthFilter jwtAuthFilter;
	private final CustomAuthenticationSuccessHandler successHandler;
	private final CustomAuthenticationFailureHandler failureHandler;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

	// =====================================================================
	// FilterChain #1: REST API (/api/**) - stateless, bảo mật bằng JWT
	// =====================================================================
	@Bean
	@Order(1)
	public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
		http
			.securityMatcher("/api/**")
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				// Public API: xem sách/danh mục/chi nhánh, đăng ký/đăng nhập/OTP
				.requestMatchers("/api/auth/**").permitAll()
				.requestMatchers(org.springframework.http.HttpMethod.GET,
						"/api/books/**", "/api/categories/**", "/api/stores/**").permitAll()
				.requestMatchers("/api/admin/**").hasRole("ADMIN")
				.requestMatchers("/api/manager/**").hasAnyRole("ADMIN", "MANAGER", "STAFF")
				.anyRequest().authenticated())
			.exceptionHandling(ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
			.authenticationProvider(authenticationProvider())
			.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	// =====================================================================
	// FilterChain #2: Toàn bộ website JSP - session-based (form login)
	// =====================================================================
	@Bean
	@Order(2)
	public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable) // đơn giản hoá cho đề án; JSP form có thể bổ sung CSRF token sau
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(
						"/", "/home", "/books", "/books/**", "/categories/**", "/stores/**",
						"/login", "/register", "/verify-otp", "/forgot-password", "/reset-password",
						"/css/**", "/js/**", "/images/**", "/webjars/**", "/ws/**", "/error")
					.permitAll()
				.requestMatchers("/admin/**").hasRole("ADMIN")
				.requestMatchers("/manager/**").hasAnyRole("ADMIN", "MANAGER", "STAFF")
				.requestMatchers("/cart/**", "/checkout/**", "/my-orders/**").hasRole("CUSTOMER")
				.anyRequest().authenticated())
			.formLogin(form -> form
				.loginPage("/login")
				.loginProcessingUrl("/login")
				.usernameParameter("username")
				.passwordParameter("password")
				.successHandler(successHandler)
				.failureHandler(failureHandler)
				.permitAll())
			.logout(logout -> logout
				.logoutUrl("/logout")
				.logoutSuccessUrl("/home?logout=1")
				.invalidateHttpSession(true)
				.deleteCookies("JSESSIONID")
				.permitAll())
			.exceptionHandling(ex -> ex.defaultAuthenticationEntryPointFor(
					(request, response, authException) -> response.sendRedirect(request.getContextPath() + "/login"),
					new AntPathRequestMatcher("/**")))
			.authenticationProvider(authenticationProvider());

		return http.build();
	}
}
