package com.doan.WineStore.dto.response.admin;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record DiscountListItemResponse(
        Long id,
        String code,
        BigDecimal discount,
        @JsonProperty("is_valid") Boolean isValid,
        @JsonProperty("times_used") Integer timesUsed) {
}
