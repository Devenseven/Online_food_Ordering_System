package com.foodordering.controller;

import com.foodordering.dto.response.CategoryResponse;
import com.foodordering.dto.response.FoodItemResponse;
import com.foodordering.dto.response.PageResponse;
import com.foodordering.service.CategoryService;
import com.foodordering.service.FoodService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PublicController {
    private final FoodService foodService;
    private final CategoryService categoryService;

    public PublicController(FoodService foodService, CategoryService categoryService) {
        this.foodService = foodService;
        this.categoryService = categoryService;
    }

    @GetMapping("/foods")
    public ResponseEntity<PageResponse<FoodItemResponse>> getFoods(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean availableOnly,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(foodService.getPublicFoods(keyword, categoryId, availableOnly, sort, page, size));
    }

    @GetMapping("/foods/{id}")
    public ResponseEntity<FoodItemResponse> getFood(@PathVariable Long id) {
        return ResponseEntity.ok(foodService.getFoodById(id));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> getCategories() {
        return ResponseEntity.ok(categoryService.getActiveCategories());
    }
}
