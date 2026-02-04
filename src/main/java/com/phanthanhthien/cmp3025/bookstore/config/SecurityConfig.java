package com.phanthanhthien.cmp3025.bookstore.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SecurityConfig - Cấu hình Spring Security
 * 
 * Cấu hình bảo mật cho hệ thống với 3 role:
 * - Anonymous (Chưa đăng nhập)
 * - USER (Người dùng thông thường)
 * - ADMIN (Quản trị viên)
 * 
 * Hỗ trợ:
 * - Form Login (username/password)
 * - OAuth2 Login (Google)
 * 
 * @author Phan Thanh Thien
 * @version 1.0.0
 * @since 2024
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Autowired
        private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

        /**
         * Cấu hình PasswordEncoder sử dụng BCrypt
         * 
         * @return BCryptPasswordEncoder instance
         */
        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        /**
         * Cấu hình UserDetailsService với mock users cho giai đoạn development
         * 
         * Mock users:
         * - user/user123 (ROLE_USER)
         * - admin/admin123 (ROLE_ADMIN)
         * 
         * @return InMemoryUserDetailsManager với mock users
         */
        @Bean
        public UserDetailsService userDetailsService() {
                // Tạo user thông thường
                UserDetails user = User.builder()
                                .username("user")
                                .password(passwordEncoder().encode("user123"))
                                .roles("USER")
                                .build();

                // Tạo admin
                UserDetails admin = User.builder()
                                .username("admin")
                                .password(passwordEncoder().encode("admin123"))
                                .roles("ADMIN", "USER")
                                .build();

                return new InMemoryUserDetailsManager(user, admin);
        }

        /**
         * Cấu hình SecurityFilterChain
         * 
         * Định nghĩa các rule bảo mật:
         * - Trang chủ, static resources: cho phép tất cả
         * - Trang quản trị: chỉ ADMIN
         * - Các trang khác: yêu cầu đăng nhập
         * 
         * @param http HttpSecurity builder
         * @return SecurityFilterChain đã cấu hình
         * @throws Exception nếu có lỗi cấu hình
         */
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                // Cấu hình authorization
                                .authorizeHttpRequests(auth -> auth
                                                // Cho phép truy cập public - trang chủ và static resources
                                                .requestMatchers("/", "/home", "/dangnhap", "/dangky").permitAll()
                                                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**")
                                                .permitAll()
                                                .requestMatchers("/error/**").permitAll()

                                                // OAuth2 endpoints
                                                .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()

                                                // API endpoints - cho phép tất cả (có thể thêm authentication sau)
                                                .requestMatchers("/api/**").permitAll()

                                                // USER & ADMIN: Cho phép XEM sách và danh mục (chỉ GET)
                                                .requestMatchers(HttpMethod.GET, "/sach", "/sach/", "/sach/chi-tiet/**")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.GET, "/danhmuc", "/danhmuc/").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/sach/timkiem").permitAll()

                                                // ADMIN ONLY: Thêm, sửa, xóa sách và danh mục
                                                .requestMatchers("/sach/them", "/sach/sua/**", "/sach/xoa/**")
                                                .hasRole("ADMIN")
                                                .requestMatchers("/danhmuc/them", "/danhmuc/sua/**", "/danhmuc/xoa/**")
                                                .hasRole("ADMIN")

                                                // Trang quản trị - chỉ ADMIN
                                                .requestMatchers("/quantri/**").hasRole("ADMIN")
                                                .requestMatchers("/nguoidung/**").hasRole("ADMIN")
                                                .requestMatchers("/apidocs/**").hasRole("ADMIN")

                                                // Giỏ hàng và thanh toán - yêu cầu đăng nhập
                                                .requestMatchers("/giohang/**").authenticated()
                                                .requestMatchers("/thanhtoan/**").authenticated()

                                                // Mua hàng - yêu cầu đăng nhập
                                                .requestMatchers("/muahang/**").authenticated()

                                                // Mặc định: yêu cầu đăng nhập
                                                .anyRequest().authenticated())

                                // Cấu hình form login
                                .formLogin(form -> form
                                                .loginPage("/dangnhap")
                                                .loginProcessingUrl("/dangnhap")
                                                .defaultSuccessUrl("/home", true)
                                                .failureUrl("/dangnhap?error=true")
                                                .usernameParameter("username")
                                                .passwordParameter("password")
                                                .permitAll())

                                // Cấu hình OAuth2 Login (Google)
                                .oauth2Login(oauth2 -> oauth2
                                                .loginPage("/dangnhap")
                                                .successHandler(oAuth2LoginSuccessHandler)
                                                .failureUrl("/dangnhap?error=oauth")
                                                .permitAll())

                                // Cấu hình logout - Cho phép GET request cho development
                                .logout(logout -> logout
                                                .logoutUrl("/dangxuat")
                                                .logoutSuccessUrl("/home?logout=true")
                                                .invalidateHttpSession(true)
                                                .deleteCookies("JSESSIONID")
                                                .permitAll())

                                // Disable CSRF cho development (BẬT LẠI trong production!)
                                .csrf(csrf -> csrf.disable())

                                // Cấu hình exception handling
                                .exceptionHandling(ex -> ex
                                                .accessDeniedPage("/access-denied"))

                                // Cấu hình Remember Me (tùy chọn)
                                .rememberMe(remember -> remember
                                                .key("bookstore-remember-me-key")
                                                .tokenValiditySeconds(86400) // 1 ngày
                                );

                return http.build();
        }

}
