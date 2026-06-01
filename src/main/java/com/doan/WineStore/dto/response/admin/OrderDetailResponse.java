package com.doan.WineStore.dto.response.admin;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderDetailResponse(
        Long id,
        @JsonProperty("order_code") String orderCode,
        String username,
        @JsonProperty("user_id") Long userId,
        BigDecimal total,
        String status,
        @JsonProperty("created_at") LocalDateTime createdAt) {
}
