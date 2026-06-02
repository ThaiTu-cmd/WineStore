package com.doan.WineStore.dto.response.admin;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class DashboardStats {
    private BigDecimal totalRevenue;
    private long totalOrders;
    private long pendingOrders;
    private long activeProducts;
    private long lowStock;
    private long totalUsers;
    private long activeDiscounts;
    private List<RecentOrder> recentOrders;
    private List<TopProduct> topProducts;
    private List<DailyRevenue> revenueLast7Days;
    private double revenueChangePercent;

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
    public long getTotalOrders() { return totalOrders; }
    public void setTotalOrders(long totalOrders) { this.totalOrders = totalOrders; }
    public long getPendingOrders() { return pendingOrders; }
    public void setPendingOrders(long pendingOrders) { this.pendingOrders = pendingOrders; }
    public long getActiveProducts() { return activeProducts; }
    public void setActiveProducts(long activeProducts) { this.activeProducts = activeProducts; }
    public long getLowStock() { return lowStock; }
    public void setLowStock(long lowStock) { this.lowStock = lowStock; }
    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
    public long getActiveDiscounts() { return activeDiscounts; }
    public void setActiveDiscounts(long activeDiscounts) { this.activeDiscounts = activeDiscounts; }
    public List<RecentOrder> getRecentOrders() { return recentOrders; }
    public void setRecentOrders(List<RecentOrder> recentOrders) { this.recentOrders = recentOrders; }
    public List<TopProduct> getTopProducts() { return topProducts; }
    public void setTopProducts(List<TopProduct> topProducts) { this.topProducts = topProducts; }
    public List<DailyRevenue> getRevenueLast7Days() { return revenueLast7Days; }
    public void setRevenueLast7Days(List<DailyRevenue> revenueLast7Days) { this.revenueLast7Days = revenueLast7Days; }
    public double getRevenueChangePercent() { return revenueChangePercent; }
    public void setRevenueChangePercent(double revenueChangePercent) { this.revenueChangePercent = revenueChangePercent; }

    public static class RecentOrder {
        private String orderCode;
        private String username;
        private BigDecimal total;
        private String status;
        private String createdAt;

        public String getOrderCode() { return orderCode; }
        public void setOrderCode(String orderCode) { this.orderCode = orderCode; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public BigDecimal getTotal() { return total; }
        public void setTotal(BigDecimal total) { this.total = total; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    }

    public static class TopProduct {
        private String name;
        private double ratingAvg;
        private int ratingCount;
        private BigDecimal price;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public double getRatingAvg() { return ratingAvg; }
        public void setRatingAvg(double ratingAvg) { this.ratingAvg = ratingAvg; }
        public int getRatingCount() { return ratingCount; }
        public void setRatingCount(int ratingCount) { this.ratingCount = ratingCount; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
    }

    public static class DailyRevenue {
        private LocalDate date;
        private BigDecimal revenue;

        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        public BigDecimal getRevenue() { return revenue; }
        public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
    }
}
