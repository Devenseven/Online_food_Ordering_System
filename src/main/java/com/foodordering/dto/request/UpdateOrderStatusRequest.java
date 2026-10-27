package com.foodordering.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(
        @NotNull(message = "Status is required")
        String status
) {
}
