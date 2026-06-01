package com.doan.WineStore.dto.response.admin;

import java.math.BigDecimal;

public record ProductDetailResponse(
        Long id,
        Long categoryId,
        String sku,
        String slug,
        String name,
        String shortDescription,
        String description,
        BigDecimal price,
        BigDecimal oldPrice,
        Double ratingAvg,
        Integer ratingCount,
        Integer stockQuantity,
        Boolean isActive) {
}
