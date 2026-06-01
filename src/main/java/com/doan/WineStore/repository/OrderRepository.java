package com.doan.WineStore.repository;

import com.doan.WineStore.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

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