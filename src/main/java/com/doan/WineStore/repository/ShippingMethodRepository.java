package com.doan.WineStore.repository;

import com.doan.WineStore.entity.ShippingMethodEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShippingMethodRepository extends JpaRepository<ShippingMethodEntity, Long> {
    List<ShippingMethodEntity> findByIsActiveTrue();
}