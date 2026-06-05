package com.doan.WineStore.service;

import com.doan.WineStore.dto.request.UserCreationRequest;
import com.doan.WineStore.dto.request.UserUpdateRequest;
import com.doan.WineStore.dto.response.admin.UserResponse;
import com.doan.WineStore.entity.User;
import com.doan.WineStore.enums.Role;
import com.doan.WineStore.enums.Status;
import com.doan.WineStore.exception.UserNotFoundException;
import com.doan.WineStore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .filter(u -> u.getDeletedAt() == null)
                .map(this::toResponse)
                .toList();
    }

    @Override
    public UserResponse createUser(UserCreationRequest request) {
        if (request.getFullName() == null || request.getFullName().isBlank()) {
            throw new IllegalArgumentException("Full name is required");
        }
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (request.getPasswordHash() == null || request.getPasswordHash().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        User user = new User();

        user.setFullName(request.getFullName().trim());
        user.setEmail(request.getEmail().trim());
        user.setPhone(request.getPhone() == null ? null : request.getPhone().trim());
        user.setRole(request.getRole() == null ? Role.CUSTOMER : request.getRole());
        user.setStatus(request.getStatus() == null ? Status.ACTIVE : request.getStatus());
        user.setPasswordHash(passwordEncoder.encode(request.getPasswordHash()));

        User savedUser = userRepository.save(user);
        return toResponse(savedUser);
    }

    @Override
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        if (request.getFullName() == null || request.getFullName().isBlank()) {
            throw new IllegalArgumentException("Full name is required");
        }
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setFullName(request.getFullName().trim());
        user.setEmail(request.getEmail().trim());
        user.setPhone(request.getPhone() == null ? null : request.getPhone().trim());

        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        if (request.getPasswordHash() != null && !request.getPasswordHash().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPasswordHash()));
        }

        User savedUser = userRepository.save(user);
        return toResponse(savedUser);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setDeletedAt(java.time.LocalDateTime.now());
        userRepository.save(user);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.getStatus());
    }
}
