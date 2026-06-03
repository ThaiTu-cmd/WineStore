package com.doan.WineStore.repository;

import com.doan.WineStore.entity.ProductImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductImageRepository extends JpaRepository<ProductImageEntity, Long> {

    List<ProductImageEntity> findByProductIdOrderBySortOrderAsc(Long productId);

    Optional<ProductImageEntity> findTopByProductIdAndIsPrimaryTrue(Long productId);

    void deleteByProductId(Long productId);
}