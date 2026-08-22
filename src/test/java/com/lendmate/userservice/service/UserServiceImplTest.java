package com.lendmate.userservice.service;

import com.lendmate.userservice.entity.User;
import com.lendmate.userservice.repository.UserRepository;
import com.lendmate.userservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;


    private User user;
//    private ProductRequest productRequest;
//    private ProductResponse productResponse;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setFirstName("Timur");
        user.setLastName("Turbil");
        user.setUsername("timurturbil");
        user.setPassword("hashedPassword");
        user.setRole("USER");
        user.setEmail("timurturbil@gmail.com");
        user.setPhone("05551234567");
        user.setProfileImage(null);
        user.setLastLoginAt(null);
        user.setDeleted(false);
        user.setVerified(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
    }

    // ─── createProduct ───────────────────────────────────────

    @Test
    void createProduct_success() {
        when(userRepository.save(user)).thenReturn(user);

        String result = userService.createUser(user);

        assertNotNull(result);
        assertEquals("User added successfully!", result);
        verify(userRepository).save(user);
    }
}
