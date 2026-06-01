package com.doan.WineStore.dto.request.admin;

import jakarta.validation.constraints.NotBlank;

public class CategoryUpsertRequest {
    @NotBlank(message = "Category name is required")
    private String name;

    @NotBlank(message = "Category slug is required")
    private String slug;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}
