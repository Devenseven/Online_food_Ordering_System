package com.foodordering.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
        Long id,
        List<CartItemResponse> items,
        BigDecimal cartTotal,
        Integer itemCount
) {
}
