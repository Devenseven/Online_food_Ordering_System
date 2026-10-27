package com.foodordering.config;

import com.foodordering.entity.enums.Role;
import com.foodordering.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
public class DataInitializer {
    @Value("${app.admin.email:admin@foodordering.com}")
    private String adminEmail;

    @Value("${app.admin.password:Admin@1234}")
    private String adminPassword;

    @Bean
    public CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.existsByEmailIgnoreCase(adminEmail)) {
                com.foodordering.entity.User admin = com.foodordering.entity.User.builder()
                        .fullName("System Admin")
                        .email(adminEmail.toLowerCase())
                        .passwordHash(passwordEncoder.encode(adminPassword))
                        .role(Role.ADMIN)
                        .createdAt(LocalDateTime.now())
                        .build();
                userRepository.save(admin);
            }
        };
    }
}
