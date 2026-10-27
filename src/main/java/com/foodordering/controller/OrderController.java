package com.foodordering.controller;

import com.foodordering.dto.request.PlaceOrderRequest;
import com.foodordering.dto.response.OrderResponse;
import com.foodordering.dto.response.OrderStatusResponse;
import com.foodordering.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(Principal principal, @Valid @RequestBody PlaceOrderRequest request) {
        OrderResponse response = orderService.placeOrder(principal.getName(), request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/my")
    public ResponseEntity<List<OrderResponse>> getMyOrders(Principal principal) {
        return ResponseEntity.ok(orderService.getMyOrders(principal.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(Principal principal, @PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(principal.getName(), id));
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<OrderStatusResponse> getOrderStatus(Principal principal, @PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderStatus(principal.getName(), id));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(Principal principal, @PathVariable Long id) {
        return ResponseEntity.ok(orderService.cancelOrder(principal.getName(), id));
    }
}
