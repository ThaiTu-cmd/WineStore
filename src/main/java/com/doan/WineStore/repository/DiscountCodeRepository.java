package com.doan.WineStore.repository;

import com.doan.WineStore.entity.DiscountCodeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountCodeRepository extends JpaRepository<DiscountCodeEntity, Long> {
    Page<DiscountCodeEntity> findAllByOrderByIdDesc(Pageable pageable);
}
