package com.foodordering.repository;

import com.foodordering.entity.FoodItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {
    @Query("SELECT f FROM FoodItem f WHERE f.active = true AND f.category.active = true " +
            "AND (:keyword IS NULL OR LOWER(f.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(f.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:categoryId IS NULL OR f.category.id = :categoryId)")
    Page<FoodItem> findPublicFoods(@Param("keyword") String keyword, @Param("categoryId") Long categoryId, Pageable pageable);

    Page<FoodItem> findByActiveTrue(Pageable pageable);
}
