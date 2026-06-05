package com.doan.WineStore.repository;

import com.doan.WineStore.entity.ProductImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ProductImageRepository extends JpaRepository<ProductImageEntity, Long> {

    List<ProductImageEntity> findByProductIdOrderBySortOrderAsc(Long productId);

    List<ProductImageEntity> findByProductIdInAndIsPrimaryTrue(List<Long> productIds);

    Optional<ProductImageEntity> findTopByProductIdAndIsPrimaryTrue(Long productId);

    @Modifying(flushAutomatically = true)
    @Transactional
    @Query("DELETE FROM ProductImageEntity p WHERE p.productId = :productId")
    void deleteByProductId(@Param("productId") Long productId);
}