package com.doan.WineStore.repository;

import com.doan.WineStore.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    Page<ProductEntity> findAllByDeletedAtIsNullOrderByIdDesc(Pageable pageable);

    List<ProductEntity> findTop4ByDeletedAtIsNullAndIsActiveTrueOrderByRatingAvgDesc();

    long countByDeletedAtIsNullAndIsActiveTrueAndStockQuantityGreaterThan(int stockQuantity);

    long countByDeletedAtIsNullAndIsActiveTrueAndStockQuantityLessThan(int stockQuantity);

    Optional<ProductEntity> findByIdAndDeletedAtIsNull(Long id);

    @Query(value = """
            SELECT p.* FROM products p
            LEFT JOIN categories c ON p.category_id = c.id
            WHERE p.deleted_at IS NULL
            AND (:categorySlug IS NULL OR c.slug = :categorySlug)
            AND (:minPrice IS NULL OR p.price >= :minPrice)
            AND (:maxPrice IS NULL OR p.price <= :maxPrice)
            """,
           countQuery = """
            SELECT COUNT(*) FROM products p
            LEFT JOIN categories c ON p.category_id = c.id
            WHERE p.deleted_at IS NULL
            AND (:categorySlug IS NULL OR c.slug = :categorySlug)
            AND (:minPrice IS NULL OR p.price >= :minPrice)
            AND (:maxPrice IS NULL OR p.price <= :maxPrice)
            """,
           nativeQuery = true)
    Page<ProductEntity> findShopProducts(@Param("categorySlug") String categorySlug,
                                          @Param("minPrice") java.math.BigDecimal minPrice,
                                          @Param("maxPrice") java.math.BigDecimal maxPrice,
                                          Pageable pageable);

    @Query(value = """
            SELECT p.* FROM products p
            WHERE p.deleted_at IS NULL
            AND (:search IS NULL OR p.name LIKE CONCAT('%', :search, '%'))
            AND (:categoryId IS NULL OR p.category_id = :categoryId)
            AND (:stockStatus IS NULL OR
                 (:stockStatus = 'low' AND p.stock_quantity < 50) OR
                 (:stockStatus = 'ok' AND p.stock_quantity >= 50))
            ORDER BY p.id DESC
            """,
           countQuery = """
            SELECT COUNT(*) FROM products p
            WHERE p.deleted_at IS NULL
            AND (:search IS NULL OR p.name LIKE CONCAT('%', :search, '%'))
            AND (:categoryId IS NULL OR p.category_id = :categoryId)
            AND (:stockStatus IS NULL OR
                 (:stockStatus = 'low' AND p.stock_quantity < 50) OR
                 (:stockStatus = 'ok' AND p.stock_quantity >= 50))
            """,
           nativeQuery = true)
    Page<ProductEntity> findAdminProducts(@Param("search") String search,
                                           @Param("categoryId") Long categoryId,
                                           @Param("stockStatus") String stockStatus,
                                           Pageable pageable);
}
