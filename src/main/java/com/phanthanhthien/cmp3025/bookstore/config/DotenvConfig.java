package com.phanthanhthien.cmp3025.bookstore.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DotenvConfig {

    @PostConstruct
    public void loadEnv() {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .directory("./")
                    .ignoreIfMalformed()
                    .ignoreIfMissing()
                    .load();

            // Set Google OAuth credentials to system properties
            String clientId = dotenv.get("GOOGLE_CLIENT_ID");
            String clientSecret = dotenv.get("GOOGLE_CLIENT_SECRET");

            if (clientId != null && !clientId.isEmpty()) {
                System.setProperty("GOOGLE_CLIENT_ID", clientId);
            }
            if (clientSecret != null && !clientSecret.isEmpty()) {
                System.setProperty("GOOGLE_CLIENT_SECRET", clientSecret);
            }

            System.out.println("✅ Loaded .env file successfully");
        } catch (Exception e) {
            System.out.println("⚠️ Could not load .env file: " + e.getMessage());
        }
    }
}
