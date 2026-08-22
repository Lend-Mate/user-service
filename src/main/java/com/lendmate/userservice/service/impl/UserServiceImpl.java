package com.lendmate.userservice.service.impl;

import com.lendmate.userservice.dto.responseDto.UserResponse;
import com.lendmate.userservice.entity.Role;
import com.lendmate.userservice.entity.User;
import com.lendmate.userservice.event.UserDeletedEvent;
import com.lendmate.userservice.mapper.UserMapper;
import com.lendmate.userservice.repository.UserRepository;
import com.lendmate.userservice.service.UserService;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder encoder;

    private final KafkaTemplate<String, UserDeletedEvent> kafkaTemplate;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder encoder, KafkaTemplate<String, UserDeletedEvent> kafka) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.encoder = encoder;
        this.kafkaTemplate = kafka;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return userMapper.toDto(user);
    }

    @Override
    public String createUser(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        user.setDeleted(false);
        user.setVerified(false);
        userRepository.save(user);
        return "User added successfully!";
    }

    @Override
    public User updateUser(Long id, User user) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
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
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        existing.setDeleted(true);
        kafkaTemplate.send("user.deleted", new UserDeletedEvent(id));
        userRepository.save(existing);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetch user from the database by email (username)
        Optional<User> userInfo = userRepository.findByEmail(username);

        if (userInfo.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }

        // Convert UserInfo to UserDetails (UserInfoDetails)
        User user = userInfo.get();

        Role role = Role.valueOf(user.getRole());
        List<SimpleGrantedAuthority> authorities = role.getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.name()))
                .toList();
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }

    @Override
    public String getEmailById(Long id) {
        return userRepository.findById(id)
                .map(User::getEmail)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }
}
