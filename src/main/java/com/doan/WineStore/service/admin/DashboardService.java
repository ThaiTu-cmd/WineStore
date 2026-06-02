package com.doan.WineStore.service.admin;

import com.doan.WineStore.dto.response.admin.DashboardStats;
import com.doan.WineStore.dto.response.admin.DashboardStats.DailyRevenue;
import com.doan.WineStore.dto.response.admin.DashboardStats.RecentOrder;
import com.doan.WineStore.dto.response.admin.DashboardStats.TopProduct;
import com.doan.WineStore.enums.Role;
import com.doan.WineStore.repository.DiscountCodeRepository;
import com.doan.WineStore.repository.OrderRepository;
import com.doan.WineStore.repository.ProductRepository;
import com.doan.WineStore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DiscountCodeRepository discountCodeRepository;

    public DashboardStats getStats() {
        DashboardStats stats = new DashboardStats();

        long totalOrders = orderRepository.count();
        long pendingOrders = orderRepository.countByStatus("pending");
        BigDecimal totalRevenue = orderRepository.sumCompletedRevenue();

        stats.setTotalOrders(totalOrders);
        stats.setPendingOrders(pendingOrders);
        stats.setTotalRevenue(totalRevenue);

        BigDecimal previousRevenue = orderRepository.sumCompletedRevenuePreviousPeriod();
        double changePct = 0;
        if (previousRevenue.compareTo(BigDecimal.ZERO) > 0) {
            changePct = totalRevenue.subtract(previousRevenue)
                    .divide(previousRevenue, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }
        stats.setRevenueChangePercent(changePct);

        String customer = Role.CUSTOMER.name();
        stats.setTotalUsers(userRepository.countByRoleAndDeletedAtIsNull(Role.CUSTOMER));

        stats.setActiveProducts(productRepository.countByDeletedAtIsNullAndIsActiveTrueAndStockQuantityGreaterThan(0));
        stats.setLowStock(productRepository.countByDeletedAtIsNullAndIsActiveTrueAndStockQuantityLessThan(50));

        stats.setActiveDiscounts(discountCodeRepository.countByIsValidTrue());

        List<RecentOrder> recentOrders = orderRepository
                .findAdminOrders(PageRequest.of(0, 5))
                .stream()
                .map(item -> {
                    RecentOrder r = new RecentOrder();
                    r.setOrderCode(item.getOrderCode());
                    r.setUsername(item.getUsername());
                    r.setTotal(item.getTotal());
                    r.setStatus(item.getStatus() == null ? null : item.getStatus().toLowerCase(Locale.ROOT));
                    r.setCreatedAt(item.getCreatedAt() == null ? null : item.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    return r;
                })
                .collect(Collectors.toList());
        stats.setRecentOrders(recentOrders);

        List<TopProduct> topProducts = productRepository
                .findTop4ByDeletedAtIsNullAndIsActiveTrueOrderByRatingAvgDesc()
                .stream()
                .map(p -> {
                    TopProduct tp = new TopProduct();
                    tp.setName(p.getName());
                    tp.setRatingAvg(p.getRatingAvg() == null ? 0.0 : p.getRatingAvg());
                    tp.setRatingCount(p.getRatingCount() == null ? 0 : p.getRatingCount());
                    tp.setPrice(p.getPrice());
                    return tp;
                })
                .collect(Collectors.toList());
        stats.setTopProducts(topProducts);

        List<DailyRevenue> revenueLast7Days = orderRepository
                .findRevenueLast7Days()
                .stream()
                .map(item -> {
                    DailyRevenue dr = new DailyRevenue();
                    dr.setDate(item.getDay());
                    dr.setRevenue(item.getRevenue());
                    return dr;
                })
                .collect(Collectors.toList());
        stats.setRevenueLast7Days(revenueLast7Days);

        return stats;
    }
}
