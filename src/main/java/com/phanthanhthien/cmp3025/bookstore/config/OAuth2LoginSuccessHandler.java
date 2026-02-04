package com.phanthanhthien.cmp3025.bookstore.config;

import com.phanthanhthien.cmp3025.bookstore.entities.User;
import com.phanthanhthien.cmp3025.bookstore.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * OAuth2 Login Success Handler - Xử lý sau khi đăng nhập Google thành công
 * In thông tin xác thực ra terminal
 * 
 * Author: Phan Thanh Thien - MSSV: 2280603036
 */
@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2LoginSuccessHandler.class);

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final UserRepository userRepository;

    public OAuth2LoginSuccessHandler(OAuth2AuthorizedClientService authorizedClientService, UserRepository userRepository) {
        this.authorizedClientService = authorizedClientService;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = oauthToken.getPrincipal();

        String clientRegistrationId = oauthToken.getAuthorizedClientRegistrationId();

        logger.info("========== GOOGLE OAUTH2 AUTHENTICATION SUCCESS ==========");
        String provider = clientRegistrationId != null ? clientRegistrationId.toUpperCase() : "UNKNOWN";
        logger.info("Provider: " + provider);
        logger.info("User Name: " + oAuth2User.getAttribute("name"));
        logger.info("Email: " + oAuth2User.getAttribute("email"));
        logger.info("Picture: " + oAuth2User.getAttribute("picture"));
        logger.info("Locale: " + oAuth2User.getAttribute("locale"));

        // Lấy Access Token
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                clientRegistrationId, oauthToken.getName());

        if (authorizedClient != null && authorizedClient.getAccessToken() != null) {
            String accessToken = authorizedClient.getAccessToken().getTokenValue();
            String tokenType = authorizedClient.getAccessToken().getTokenType().getValue();

            logger.info("Token Type: " + tokenType);
            logger.info("Access Token: " + accessToken);
            logger.info("Token Expires: " + authorizedClient.getAccessToken().getExpiresAt());

            // Refresh Token (neu co)
            if (authorizedClient.getRefreshToken() != null) {
                logger.info("Refresh Token: " + authorizedClient.getRefreshToken().getTokenValue());
            }
        }

        logger.info("---------- All Attributes ----------");
        oAuth2User.getAttributes().forEach((key, value) -> {
            logger.info("  " + key + " = " + value);
        });
        logger.info("====================================");

        // Luu hoac cap nhat user vao MongoDB
        saveOrUpdateUser(oAuth2User, provider);

        // Redirect về trang chủ
        response.sendRedirect("/");
    }

    private void saveOrUpdateUser(OAuth2User oAuth2User, String provider) {
        try {
            String email = oAuth2User.getAttribute("email");
            String providerId = oAuth2User.getAttribute("sub");
            String name = oAuth2User.getAttribute("name");
            String firstName = oAuth2User.getAttribute("given_name");
            String lastName = oAuth2User.getAttribute("family_name");
            String picture = oAuth2User.getAttribute("picture");

            Optional<User> existingUser = userRepository.findByEmail(email);
            User user;

            if (existingUser.isPresent()) {
                // Cap nhat user da ton tai
                user = existingUser.get();
                user.setName(name);
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setPicture(picture);
                user.setLastLoginAt(LocalDateTime.now());
                logger.info("✅ Updated existing user: " + email);
            } else {
                // Tao user moi
                user = new User(email, name, provider, providerId);
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setPicture(picture);
                user.setLastLoginAt(LocalDateTime.now());
                logger.info("✅ Created new user: " + email);
            }

            userRepository.save(user);

        } catch (Exception e) {
            logger.error("❌ Error saving user to MongoDB: " + e.getMessage());
        }
    }
}
