package com.doan.WineStore.repository;

import com.doan.WineStore.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query(value = """
            SELECT o.id AS id,
                   o.order_code AS orderCode,
                   COALESCE(u.full_name, u.email, CONCAT('User #', o.user_id)) AS username,
                   o.user_id AS userId,
                   o.total_amount AS total,
                   LOWER(o.status) AS status,
                   o.created_at AS createdAt
            FROM orders o
            LEFT JOIN users u ON u.id = o.user_id
            ORDER BY o.id DESC
            """, countQuery = "SELECT COUNT(*) FROM orders", nativeQuery = true)
    Page<OrderListProjection> findAdminOrders(Pageable pageable);

    @Query(value = """
            SELECT o.id AS id,
                   o.order_code AS orderCode,
                   COALESCE(u.full_name, u.email, CONCAT('User #', o.user_id)) AS username,
                   o.user_id AS userId,
                   o.total_amount AS total,
                   LOWER(o.status) AS status,
                   o.created_at AS createdAt
            FROM orders o
            LEFT JOIN users u ON u.id = o.user_id
            WHERE o.id = :id
            """, nativeQuery = true)
    OrderListProjection findAdminOrderById(@Param("id") Long id);

    @Query(value = "SELECT COUNT(*) FROM orders WHERE LOWER(status) = :status", nativeQuery = true)
    long countByStatus(@Param("status") String status);

    @Query(value = "SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE LOWER(status) = 'completed'", nativeQuery = true)
    BigDecimal sumCompletedRevenue();

    @Query(value = "SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE LOWER(status) = 'completed' AND created_at >= CURDATE() - INTERVAL 13 DAY AND created_at < CURDATE() - INTERVAL 6 DAY", nativeQuery = true)
    BigDecimal sumCompletedRevenuePreviousPeriod();

    @Query(value = """
            SELECT DATE(created_at) AS day, COALESCE(SUM(total_amount), 0) AS revenue
            FROM orders WHERE LOWER(status) = 'completed' AND created_at >= CURDATE() - INTERVAL 6 DAY
            GROUP BY DATE(created_at) ORDER BY DATE(created_at)
            """, nativeQuery = true)
    List<DailyRevenueProjection> findRevenueLast7Days();

    interface DailyRevenueProjection {
        LocalDate getDay();
        BigDecimal getRevenue();
    }

    interface OrderListProjection {
        Long getId();
        String getOrderCode();
        String getUsername();
        Long getUserId();
        java.math.BigDecimal getTotal();
        String getStatus();
        java.time.LocalDateTime getCreatedAt();
    }
}