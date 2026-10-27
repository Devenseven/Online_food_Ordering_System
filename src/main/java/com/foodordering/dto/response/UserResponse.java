package com.foodordering.dto.response;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String fullName,
        String email,
        String role,
        LocalDateTime createdAt
) {
}
