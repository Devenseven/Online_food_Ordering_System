package com.foodordering.service;

import com.foodordering.dto.request.RegisterRequest;
import com.foodordering.dto.response.UserResponse;
import com.foodordering.entity.User;
import com.foodordering.entity.enums.Role;
import com.foodordering.exception.ConflictException;
import com.foodordering.mapper.EntityMapper;
import com.foodordering.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityMapper mapper;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, EntityMapper mapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ConflictException("Email already exists");
        }
        User user = User.builder()
                .fullName(request.fullName())
                .email(request.email().toLowerCase())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(Role.CUSTOMER)
                .createdAt(LocalDateTime.now())
                .build();
        User saved = userRepository.save(user);
        return mapper.toUserResponse(saved);
    }

    public User getCurrentUser(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new com.foodordering.exception.ResourceNotFoundException("User not found"));
    }
}
