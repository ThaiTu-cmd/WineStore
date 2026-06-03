package com.doan.WineStore.repository;

import com.doan.WineStore.entity.PaymentMethodEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethodEntity, Long> {
    List<PaymentMethodEntity> findByUserIdAndDeletedAtIsNull(Long userId);
}