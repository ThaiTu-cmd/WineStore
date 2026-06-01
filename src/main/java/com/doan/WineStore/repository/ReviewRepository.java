package com.doan.WineStore.repository;

import com.doan.WineStore.entity.ReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    @Query(value = """
            SELECT r.id AS id,
                   COALESCE(u.full_name, u.email, CONCAT('User #', r.user_id)) AS user,
                   COALESCE(p.name, CONCAT('Product #', r.product_id)) AS product,
                   COALESCE(r.rating, 0) AS rating,
                   COALESCE(r.comment, '') AS comment
            FROM reviews r
            LEFT JOIN users u ON u.id = r.user_id
            LEFT JOIN products p ON p.id = r.product_id
            ORDER BY r.id DESC
            """, countQuery = "SELECT COUNT(*) FROM reviews", nativeQuery = true)
    Page<ReviewListProjection> findAdminReviews(Pageable pageable);

    interface ReviewListProjection {
        Long getId();

        String getUser();

        String getProduct();

        Integer getRating();

        String getComment();
    }
}
