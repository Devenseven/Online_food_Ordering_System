package com.foodordering.controller;

import com.foodordering.dto.request.CategoryRequest;
import com.foodordering.dto.request.FoodItemRequest;
import com.foodordering.dto.request.UpdateOrderStatusRequest;
import com.foodordering.dto.response.*;
import com.foodordering.entity.enums.OrderStatus;
import com.foodordering.service.CategoryService;
import com.foodordering.service.DashboardService;
import com.foodordering.service.FoodService;
import com.foodordering.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final FoodService foodService;
    private final CategoryService categoryService;
    private final OrderService orderService;
    private final DashboardService dashboardService;

    public AdminController(FoodService foodService, CategoryService categoryService, OrderService orderService, DashboardService dashboardService) {
        this.foodService = foodService;
        this.categoryService = categoryService;
        this.orderService = orderService;
        this.dashboardService = dashboardService;
    }

    // Foods
    @PostMapping("/foods")
    public ResponseEntity<FoodItemResponse> createFood(@Valid @RequestBody FoodItemRequest request) {
        return new ResponseEntity<>(foodService.createFood(request), HttpStatus.CREATED);
    }

    @PutMapping("/foods/{id}")
    public ResponseEntity<FoodItemResponse> updateFood(@PathVariable Long id, @Valid @RequestBody FoodItemRequest request) {
        return ResponseEntity.ok(foodService.updateFood(id, request));
    }

    @PatchMapping("/foods/{id}/availability")
    public ResponseEntity<FoodItemResponse> updateAvailability(@PathVariable Long id, @RequestParam boolean available) {
        return ResponseEntity.ok(foodService.updateAvailability(id, available));
    }

    @DeleteMapping("/foods/{id}")
    public ResponseEntity<Void> deleteFood(@PathVariable Long id) {
        foodService.deleteFood(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/foods")
    public ResponseEntity<PageResponse<FoodItemResponse>> getAllFoods(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort) {
        return ResponseEntity.ok(foodService.getAllFoodsForAdmin(page, size, sort));
    }

    // Categories
    @PostMapping("/categories")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        return new ResponseEntity<>(categoryService.create(request), HttpStatus.CREATED);
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.update(id, request));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    // Orders
    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getOrders(@RequestParam(required = false) String status) {
        OrderStatus orderStatus = null;
        if (status != null && !status.isBlank()) {
            try {
                orderStatus = OrderStatus.valueOf(status);
            } catch (Exception ignored) {
            }
        }
        return ResponseEntity.ok(orderService.getAllOrders(orderStatus));
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderByIdForAdmin(id));
    }

    @PatchMapping("/orders/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable Long id, @Valid @RequestBody UpdateOrderStatusRequest request) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, request.status()));
    }

    // Dashboard
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboard());
    }
}
