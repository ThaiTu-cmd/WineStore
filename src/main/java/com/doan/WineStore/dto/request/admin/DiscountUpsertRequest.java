package com.doan.WineStore.dto.request.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Max;

public class DiscountUpsertRequest {
    @NotBlank(message = "Discount code is required")
    private String code;

    @NotNull(message = "Discount value is required")
    @Positive(message = "Discount must be positive")
    @Max(value = 100, message = "Discount cannot exceed 100%")
    private Integer discount;

    @JsonProperty("is_valid")
    private Boolean isValid = true;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public Boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
    }
}
