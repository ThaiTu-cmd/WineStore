package com.doan.WineStore.dto.response.admin;

import java.math.BigDecimal;

public record ProductListItemResponse(
        Long id,
        Long categoryId,
        String name,
        BigDecimal price,
        Double ratingAvg,
        Integer ratingCount,
        Integer stockQuantity) {
}
