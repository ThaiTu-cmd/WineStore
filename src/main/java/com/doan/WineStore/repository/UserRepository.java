package com.doan.WineStore.repository;

import com.doan.WineStore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailOrPhone(String email, String phone);

    Optional<User> findByEmail(String email);

    boolean existsByRoleAndDeletedAtIsNull(com.doan.WineStore.enums.Role role);
}
