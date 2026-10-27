package com.foodordering.service;

import com.foodordering.dto.request.FoodItemRequest;
import com.foodordering.dto.response.FoodItemResponse;
import com.foodordering.dto.response.PageResponse;
import com.foodordering.entity.Category;
import com.foodordering.entity.FoodItem;
import com.foodordering.exception.ResourceNotFoundException;
import com.foodordering.mapper.EntityMapper;
import com.foodordering.repository.CategoryRepository;
import com.foodordering.repository.FoodItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FoodService {
    private final FoodItemRepository foodItemRepository;
    private final CategoryRepository categoryRepository;
    private final EntityMapper mapper;

    public FoodService(FoodItemRepository foodItemRepository, CategoryRepository categoryRepository, EntityMapper mapper) {
        this.foodItemRepository = foodItemRepository;
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public PageResponse<FoodItemResponse> getPublicFoods(String keyword, Long categoryId, Boolean availableOnly,
                                                         String sort, int page, int size) {
        Pageable pageable = buildPageable(sort, page, size);
        Page<FoodItem> foodPage = foodItemRepository.findPublicFoods(keyword, categoryId, pageable);
        Page<FoodItemResponse> responsePage = foodPage.map(mapper::toFoodItemResponse);
        return PageResponse.from(responsePage);
    }

    @Transactional(readOnly = true)
    public FoodItemResponse getFoodById(Long id) {
        FoodItem food = foodItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found"));
        return mapper.toFoodItemResponse(food);
    }

    @Transactional(readOnly = true)
    public PageResponse<FoodItemResponse> getAllFoodsForAdmin(int page, int size, String sort) {
        Pageable pageable = buildPageable(sort, page, size);
        Page<FoodItem> foodPage = foodItemRepository.findAll(pageable);
        Page<FoodItemResponse> responsePage = foodPage.map(mapper::toFoodItemResponse);
        return PageResponse.from(responsePage);
    }

    @Transactional
    public FoodItemResponse createFood(FoodItemRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        FoodItem food = FoodItem.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .imageUrl(request.imageUrl())
                .available(request.available() == null ? true : request.available())
                .active(request.active() == null ? true : request.active())
                .category(category)
                .build();
        return mapper.toFoodItemResponse(foodItemRepository.save(food));
    }

    @Transactional
    public FoodItemResponse updateFood(Long id, FoodItemRequest request) {
        FoodItem food = foodItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found"));
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        food.setName(request.name());
        food.setDescription(request.description());
        food.setPrice(request.price());
        food.setImageUrl(request.imageUrl());
        if (request.available() != null) food.setAvailable(request.available());
        if (request.active() != null) food.setActive(request.active());
        food.setCategory(category);
        return mapper.toFoodItemResponse(foodItemRepository.save(food));
    }

    @Transactional
    public FoodItemResponse updateAvailability(Long id, boolean available) {
        FoodItem food = foodItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found"));
        food.setAvailable(available);
        return mapper.toFoodItemResponse(foodItemRepository.save(food));
    }

    @Transactional
    public void deleteFood(Long id) {
        FoodItem food = foodItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found"));
        food.setActive(false);
        foodItemRepository.save(food);
    }

    private Pageable buildPageable(String sort, int page, int size) {
        if (size <= 0) size = 12;
        if (page < 0) page = 0;
        if (sort == null || sort.isBlank()) {
            return PageRequest.of(page, size);
        }
        String[] parts = sort.split(",");
        String field = parts[0];
        Sort.Direction direction = parts.length > 1 && "desc".equalsIgnoreCase(parts[1]) ? Sort.Direction.DESC : Sort.Direction.ASC;
        if ("price".equals(field) || "name".equals(field)) {
            return PageRequest.of(page, size, Sort.by(direction, field));
        }
        return PageRequest.of(page, size);
    }
}
