package com.project.user_service.service;

import com.project.user_service.dto.responseDto.UserResponse;
import com.project.user_service.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> getAllUsers();

    UserResponse getUserById(Long id);

    String createUser(User user);

    User updateUser(Long id, User user);

    void deleteUser(Long id);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByPhone(String phone);
    String getEmailById(Long id);
}
