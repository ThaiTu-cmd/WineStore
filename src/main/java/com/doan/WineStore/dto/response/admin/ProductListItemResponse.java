package com.doan.WineStore.dto.response.admin;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record ProductListItemResponse(
        Long id,
        @JsonProperty("category_id") Long categoryId,
        String name,
        @JsonProperty("image_url") String imageUrl,
        String brand,
        BigDecimal price,
        @JsonProperty("rating_avg") Double ratingAvg,
        @JsonProperty("rating_count") Integer ratingCount,
        @JsonProperty("stock_quantity") Integer stockQuantity) {
}
