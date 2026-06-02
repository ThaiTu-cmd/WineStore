package com.doan.WineStore.dto.request.admin;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class ProductUpsertRequest {
    @JsonProperty("category_id")
    private Long categoryId;
    private String sku;
    private String slug;
    private String name;
    @JsonProperty("image_url")
    private String imageUrl;
    private String brand;
    @JsonProperty("short_description")
    private String shortDescription;
    private String description;
    private BigDecimal price;
    @JsonProperty("old_price")
    private BigDecimal oldPrice;
    @JsonProperty("stock_quantity")
    private Integer stockQuantity;
    @JsonProperty("is_active")
    private Boolean isActive;

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(BigDecimal oldPrice) {
        this.oldPrice = oldPrice;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }
}
