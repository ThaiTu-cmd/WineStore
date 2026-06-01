package com.doan.WineStore.dto.response.admin;

public record ReviewListItemResponse(
        Long id,
        String user,
        String product,
        Integer rating,
        String comment) {
}
