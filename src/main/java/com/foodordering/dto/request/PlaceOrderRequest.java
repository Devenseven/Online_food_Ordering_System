package com.foodordering.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PlaceOrderRequest(
        @NotBlank(message = "Delivery address is required")
        @Size(max = 1000, message = "Delivery address must be at most 1000 characters")
        String deliveryAddress,

        @NotBlank(message = "Contact number is required")
        @Pattern(regexp = "^[0-9+\\-\\s()]{7,20}$", message = "Invalid contact number")
        String contactNumber,

        @Size(max = 255, message = "Delivery instructions must be at most 255 characters")
        String deliveryInstructions,

        @NotBlank(message = "Payment method is required")
        String paymentMethod
) {
}
