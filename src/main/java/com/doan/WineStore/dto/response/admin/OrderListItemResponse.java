package com.doan.WineStore.dto.response.admin;

import java.math.BigDecimal;

public record OrderListItemResponse(
        Long id,
        String orderCode,
        String username,
        Long userId,
        BigDecimal total,
        String status) {
}