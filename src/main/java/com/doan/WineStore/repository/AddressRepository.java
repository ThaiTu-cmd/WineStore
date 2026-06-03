package com.doan.WineStore.repository;

import com.doan.WineStore.entity.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AddressRepository extends JpaRepository<AddressEntity, Long> {

    @Query("SELECT a FROM AddressEntity a WHERE a.userId = :userId AND a.deletedAt IS NULL ORDER BY a.isDefault DESC, a.id DESC")
    List<AddressEntity> findByUserIdOrderByIsDefaultDesc(@Param("userId") Long userId);

    @Query("SELECT a FROM AddressEntity a WHERE a.id = :id AND a.userId = :userId AND a.deletedAt IS NULL")
    AddressEntity findByIdAndUserIdAndDeletedAtIsNull(@Param("id") Long id, @Param("userId") Long userId);

    @Query("SELECT COUNT(a) FROM AddressEntity a WHERE a.userId = :userId AND a.deletedAt IS NULL")
    long countByUserId(@Param("userId") Long userId);
}
