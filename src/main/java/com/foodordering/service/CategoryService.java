package com.foodordering.service;

import com.foodordering.dto.response.CategoryResponse;
import com.foodordering.entity.Category;
import com.foodordering.exception.ResourceNotFoundException;
import com.foodordering.mapper.EntityMapper;
import com.foodordering.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final EntityMapper mapper;

    public CategoryService(CategoryRepository categoryRepository, EntityMapper mapper) {
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getActiveCategories() {
        return mapper.toCategoryResponseList(categoryRepository.findByActiveTrue());
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return mapper.toCategoryResponseList(categoryRepository.findAll());
    }

    @Transactional
    public CategoryResponse create(com.foodordering.dto.request.CategoryRequest request) {
        Category category = Category.builder()
                .name(request.name())
                .description(request.description())
                .active(request.active() == null ? true : request.active())
                .build();
        return mapper.toCategoryResponse(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse update(Long id, com.foodordering.dto.request.CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        category.setName(request.name());
        category.setDescription(request.description());
        if (request.active() != null) {
            category.setActive(request.active());
        }
        return mapper.toCategoryResponse(categoryRepository.save(category));
    }

    @Transactional
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        category.setActive(false);
        categoryRepository.save(category);
    }
}
