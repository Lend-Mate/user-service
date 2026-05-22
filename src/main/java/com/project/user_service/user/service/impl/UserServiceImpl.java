package com.project.user_service.user.service.impl;

import com.project.user_service.user.entity.User;
import com.project.user_service.user.repository.UserRepository;
import com.project.user_service.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    @Override
    public User createUser(User user) {
        user.setDeleted(false);
        user.setVerified(false);
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User user) {
        User existing = getUserById(id);
        existing.setFirstName(user.getFirstName());
        existing.setLastName(user.getLastName());
        existing.setUsername(user.getUsername());
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            existing.setPassword(user.getPassword());
        }
        existing.setRole(user.getRole());
        existing.setLastLoginAt(user.getLastLoginAt());
        existing.setProfileImage(user.getProfileImage());
        existing.setPhone(user.getPhone());
        existing.setEmail(user.getEmail());
        existing.setVerified(user.isVerified());
        return userRepository.save(existing);
    }

    @Override
    public void deleteUser(Long id) {
        User existing = getUserById(id);
        existing.setDeleted(true);
        userRepository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }
}
