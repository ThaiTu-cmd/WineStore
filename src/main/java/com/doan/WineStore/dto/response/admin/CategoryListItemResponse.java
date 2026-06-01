package com.doan.WineStore.dto.response.admin;

public record CategoryListItemResponse(
        Long id,
        String name,
        String slug,
        Long products) {
}
