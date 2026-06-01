package com.doan.WineStore.repository;

import com.doan.WineStore.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    Page<ProductEntity> findAllByDeletedAtIsNullOrderByIdDesc(Pageable pageable);

    Optional<ProductEntity> findByIdAndDeletedAtIsNull(Long id);
}
