package com.foodordering.controller;

import com.foodordering.dto.request.LoginRequest;
import com.foodordering.dto.request.RegisterRequest;
import com.foodordering.dto.response.UserResponse;
import com.foodordering.exception.UnauthorizedException;
import com.foodordering.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    public AuthController(AuthService authService, AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email().toLowerCase(), request.password())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
            repo.saveContext(SecurityContextHolder.getContext(), httpRequest, httpRequest.getResponse());
            com.foodordering.entity.User user = authService.getCurrentUser(authentication.getName());
            return ResponseEntity.ok(new UserResponse(user.getId(), user.getFullName(), user.getEmail(), user.getRole().name(), user.getCreatedAt()));
        } catch (Exception e) {
            throw new UnauthorizedException("Invalid email or password");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        request.getSession().invalidate();
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(Principal principal) {
        if (principal == null) {
            throw new UnauthorizedException("Not authenticated");
        }
        com.foodordering.entity.User user = authService.getCurrentUser(principal.getName());
        return ResponseEntity.ok(new UserResponse(user.getId(), user.getFullName(), user.getEmail(), user.getRole().name(), user.getCreatedAt()));
    }
}
