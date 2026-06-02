package com.doan.WineStore.dto.response.admin;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record ProductDetailResponse(
        Long id,
        @JsonProperty("category_id") Long categoryId,
        String sku,
        String slug,
        String name,
        @JsonProperty("image_url") String imageUrl,
        String brand,
        @JsonProperty("short_description") String shortDescription,
        String description,
        BigDecimal price,
        @JsonProperty("old_price") BigDecimal oldPrice,
        @JsonProperty("rating_avg") Double ratingAvg,
        @JsonProperty("rating_count") Integer ratingCount,
        @JsonProperty("stock_quantity") Integer stockQuantity,
        @JsonProperty("is_active") Boolean isActive) {
}
