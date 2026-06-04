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
        @JsonProperty("created_at") LocalDateTime createdAt,
        @JsonProperty("shipping_address_id") Long shippingAddressId,
        @JsonProperty("payment_method_id") Long paymentMethodId,
        @JsonProperty("shipping_method_id") Long shippingMethodId,
        @JsonProperty("discount_code_id") Long discountCodeId,
        BigDecimal subtotal,
        @JsonProperty("total_amount") BigDecimal totalAmount) {
}
