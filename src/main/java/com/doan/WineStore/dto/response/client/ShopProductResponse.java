package com.doan.WineStore.dto.response.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ShopProductResponse {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private String categorySlug;
    private String name;
    private String imageUrl;
    private String brand;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private int discountPercent;
    private boolean isNew;
    private double ratingAvg;
    private int reviewCount;
    private LocalDateTime createdAt;

    public ShopProductResponse() {}

    public ShopProductResponse(Long id, Long categoryId, String categoryName, String categorySlug,
                               String name, String imageUrl, String brand,
                               BigDecimal price, BigDecimal originalPrice,
                               double ratingAvg, int reviewCount, LocalDateTime createdAt) {
        this.id = id;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categorySlug = categorySlug;
        this.name = name;
        this.imageUrl = imageUrl;
        this.brand = brand;
        this.price = price;
        this.originalPrice = originalPrice;
        this.ratingAvg = ratingAvg;
        this.reviewCount = reviewCount;
        this.createdAt = createdAt;
        this.discountPercent = computeDiscountPercent(price, originalPrice);
        this.isNew = createdAt != null && createdAt.isAfter(LocalDateTime.now().minusDays(30));
    }

    private int computeDiscountPercent(BigDecimal price, BigDecimal oldPrice) {
        if (price == null || oldPrice == null || oldPrice.compareTo(BigDecimal.ZERO) <= 0
                || price.compareTo(oldPrice) >= 0) {
            return 0;
        }
        return oldPrice.subtract(price)
                .multiply(BigDecimal.valueOf(100))
                .divide(oldPrice, BigDecimal.ROUND_HALF_UP)
                .intValue();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getCategorySlug() { return categorySlug; }
    public void setCategorySlug(String categorySlug) { this.categorySlug = categorySlug; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }
    public int getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(int discountPercent) { this.discountPercent = discountPercent; }
    public boolean getIsNew() { return isNew; }
    public void setIsNew(boolean isNew) { this.isNew = isNew; }
    public double getRatingAvg() { return ratingAvg; }
    public void setRatingAvg(double ratingAvg) { this.ratingAvg = ratingAvg; }
    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
