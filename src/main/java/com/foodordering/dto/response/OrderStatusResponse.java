package com.foodordering.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderStatusResponse(
        Long id,
        String status,
        LocalDateTime orderDate,
        BigDecimal totalAmount
) {
}
