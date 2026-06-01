package com.doan.WineStore.dto.response.admin;

import java.math.BigDecimal;

public record DiscountListItemResponse(
        Long id,
        String code,
        BigDecimal discount,
        Boolean isValid,
        Integer timesUsed) {
}
