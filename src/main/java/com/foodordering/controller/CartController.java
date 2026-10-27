package com.foodordering.controller;

import com.foodordering.dto.request.AddCartItemRequest;
import com.foodordering.dto.request.UpdateCartItemRequest;
import com.foodordering.dto.response.CartResponse;
import com.foodordering.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart(Principal principal) {
        return ResponseEntity.ok(cartService.getOrCreateCart(principal.getName()));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(Principal principal, @Valid @RequestBody AddCartItemRequest request) {
        return ResponseEntity.ok(cartService.addItem(principal.getName(), request));
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> updateItem(Principal principal, @PathVariable Long cartItemId, @Valid @RequestBody UpdateCartItemRequest request) {
        return ResponseEntity.ok(cartService.updateItem(principal.getName(), cartItemId, request));
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Void> removeItem(Principal principal, @PathVariable Long cartItemId) {
        cartService.removeItem(principal.getName(), cartItemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(Principal principal) {
        cartService.clearCart(principal.getName());
        return ResponseEntity.noContent().build();
    }
}
