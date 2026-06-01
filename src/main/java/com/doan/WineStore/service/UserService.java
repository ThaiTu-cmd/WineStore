package com.doan.WineStore.service;

import com.doan.WineStore.dto.request.UserCreationRequest;
import com.doan.WineStore.dto.request.UserUpdateRequest;
import com.doan.WineStore.dto.response.admin.UserResponse;

import java.util.List;

public interface UserService {
    List<UserResponse> getAllUsers();

    UserResponse createUser(UserCreationRequest request);

    UserResponse updateUser(Long id, UserUpdateRequest request);

    void deleteUser(Long id);
}
