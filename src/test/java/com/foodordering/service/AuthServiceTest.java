package com.foodordering.service;

import com.foodordering.dto.request.RegisterRequest;
import com.foodordering.dto.response.UserResponse;
import com.foodordering.entity.User;
import com.foodordering.entity.enums.Role;
import com.foodordering.exception.ConflictException;
import com.foodordering.mapper.EntityMapper;
import com.foodordering.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private EntityMapper mapper;
    @InjectMocks
    private AuthService authService;

    @Test
    void duplicateEmailThrowsException() {
        when(userRepository.existsByEmailIgnoreCase("test@test.com")).thenReturn(true);
        RegisterRequest request = new RegisterRequest("Test User", "test@test.com", "Password123");
        assertThrows(ConflictException.class, () -> authService.register(request));
    }

    @Test
    void passwordIsHashed() {
        RegisterRequest request = new RegisterRequest("Test User", "test@test.com", "Password123");
        User user = User.builder().id(1L).fullName("Test User").email("test@test.com").role(Role.CUSTOMER).build();
        UserResponse response = new UserResponse(1L, "Test User", "test@test.com", "CUSTOMER", null);

        when(userRepository.existsByEmailIgnoreCase("test@test.com")).thenReturn(false);
        when(passwordEncoder.encode("Password123")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(mapper.toUserResponse(user)).thenReturn(response);

        authService.register(request);

        verify(passwordEncoder).encode("Password123");
    }
}
