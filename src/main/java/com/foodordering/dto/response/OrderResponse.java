package com.foodordering.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        Long userId,
        String userName,
        String userEmail,
        LocalDateTime orderDate,
        String status,
        String deliveryAddress,
        String contactNumber,
        String deliveryInstructions,
        BigDecimal subtotal,
        BigDecimal deliveryFee,
        BigDecimal totalAmount,
        String paymentMethod,
        String paymentStatus,
        List<OrderItemResponse> items
) {
}
