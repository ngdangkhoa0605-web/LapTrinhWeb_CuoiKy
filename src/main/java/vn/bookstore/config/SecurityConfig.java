
package vn.bookstore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import lombok.RequiredArgsConstructor;
import vn.bookstore.security.JwtAuthFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final org.springframework.security.core.userdetails.UserDetailsService userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;

    // =====================================================
    // PASSWORD ENCODER
    // =====================================================

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // =====================================================
    // AUTHENTICATION PROVIDER
    // =====================================================

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();

        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    // =====================================================
    // AUTHENTICATION MANAGER
    // =====================================================

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    // =====================================================
    // API SECURITY - JWT
    // =====================================================

    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurity(HttpSecurity http) throws Exception {

        http
            .securityMatcher(
                new AntPathRequestMatcher("/api/**")
            )

            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth

                // API authentication
                .requestMatchers(
                    new AntPathRequestMatcher("/api/auth/**")
                ).permitAll()

                // API GET public
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/books/**",
                    "/api/categories/**",
                    "/api/stores/**"
                ).permitAll()

                // ADMIN API
                .requestMatchers(
                    new AntPathRequestMatcher("/api/admin/**")
                ).hasRole("ADMIN")

                // Các API còn lại cần đăng nhập
                .anyRequest().authenticated()
            )

            .authenticationProvider(authenticationProvider())

            .addFilterBefore(
                jwtAuthFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    // =====================================================
    // WEB SECURITY - SESSION + JSP
    // =====================================================

    @Bean
    @Order(2)
    public SecurityFilterChain webSecurity(HttpSecurity http)
            throws Exception {

        http

            // Tất cả request web
            .securityMatcher(
                new AntPathRequestMatcher("/**")
            )

            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth

                // =================================================
                // PUBLIC URL
                // =================================================

                .requestMatchers(
                    new AntPathRequestMatcher("/"),
                    new AntPathRequestMatcher("/home"),
                    new AntPathRequestMatcher("/login"),
                    new AntPathRequestMatcher("/register"),
                    new AntPathRequestMatcher("/verify-otp"),
                    new AntPathRequestMatcher("/forgot-password"),
                    new AntPathRequestMatcher("/reset-password"),

                    new AntPathRequestMatcher("/books"),
                    new AntPathRequestMatcher("/books/**"),
                    new AntPathRequestMatcher("/categories/**"),
                    new AntPathRequestMatcher("/stores/**"),

                    // Static resources
                    new AntPathRequestMatcher("/css/**"),
                    new AntPathRequestMatcher("/js/**"),
                    new AntPathRequestMatcher("/images/**"),
                    new AntPathRequestMatcher("/webjars/**"),

                    // Error page
                    new AntPathRequestMatcher("/error"),

                    // JSP được forward bởi Controller
                    new AntPathRequestMatcher("/WEB-INF/views/**")
                )
                .permitAll()

                // =================================================
                // ADMIN
                // =================================================

                .requestMatchers(
                    new AntPathRequestMatcher("/admin/**")
                )
                .hasRole("ADMIN")

                // =================================================
                // MANAGER / STAFF
                // =================================================

                .requestMatchers(
                    new AntPathRequestMatcher("/manager/**")
                )
                .hasAnyRole(
                    "ADMIN",
                    "MANAGER",
                    "STAFF"
                )

                // =================================================
                // CUSTOMER
                // =================================================

                .requestMatchers(
                    new AntPathRequestMatcher("/cart/**"),
                    new AntPathRequestMatcher("/checkout/**"),
                    new AntPathRequestMatcher("/my-orders/**")
                )
                .hasRole("CUSTOMER")

                // =================================================
                // CÒN LẠI
                // =================================================

                .anyRequest()
                .authenticated()
            )

            // =====================================================
            // FORM LOGIN
            // =====================================================

            .formLogin(form -> form

                .loginPage("/login")

                .loginProcessingUrl("/login")

                .usernameParameter("username")

                .passwordParameter("password")

                .defaultSuccessUrl("/home", true)

                .failureUrl("/login?error=1")

                .permitAll()
            )

            // =====================================================
            // LOGOUT
            // =====================================================

            .logout(logout -> logout

                .logoutUrl("/logout")

                .logoutSuccessUrl("/home?logout=1")

                .invalidateHttpSession(true)

                .deleteCookies("JSESSIONID")

                .permitAll()
            )

            // =====================================================
            // AUTHENTICATION PROVIDER
            // =====================================================

            .authenticationProvider(authenticationProvider());

        return http.build();
    }
}
