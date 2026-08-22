package com.lendmate.userservice.service;

import com.lendmate.userservice.entity.User;
import com.lendmate.userservice.repository.UserRepository;
import com.lendmate.userservice.service.impl.JwtServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    @InjectMocks
    private JwtServiceImpl jwtService;


    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
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
    void generateToken_success() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.ofNullable(user));

        String token = jwtService.generateToken(user.getEmail());
        assertNotNull(token);

        String email = jwtService.extractUsername(token);
        assertEquals(user.getEmail(), email);

        Claims claims = jwtService.extractClaim(token, claimsObj -> claimsObj);

        assertEquals(user.getRole(), claims.get("role"));
        assertEquals(user.getId().toString(), claims.get("userId"));

        verify(userRepository).findByEmail(user.getEmail());
    }


    @Test
    void validateToken_validToken_returnsTrue() {
        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        String token = jwtService.generateToken(user.getEmail());

        UserDetails userDetails =
                new org.springframework.security.core.userdetails.User(
                        user.getEmail(),
                        user.getPassword(),
                        Collections.emptyList()
                );

        Boolean result = jwtService.validateToken(token, userDetails);

        assertTrue(result);
    }

    @Test
    void validateToken_wrongUser_returnsFalse() {
        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        String token = jwtService.generateToken(user.getEmail());

        UserDetails userDetails =
                new org.springframework.security.core.userdetails.User(
                        "another@gmail.com",
                        "password",
                        Collections.emptyList()
                );

        Boolean result = jwtService.validateToken(token, userDetails);

        assertFalse(result);
    }

    @Test
    void validateToken_expiredToken_returnsFalse() {
        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));
        doReturn(-1000L).when(jwtService).getExpirationTime();

        String token = jwtService.generateToken(user.getEmail());

        UserDetails userDetails =
                new org.springframework.security.core.userdetails.User(
                        user.getEmail(),
                        user.getPassword(),
                        Collections.emptyList()
                );

        assertThrows(
                ExpiredJwtException.class,
                () -> jwtService.validateToken(token, userDetails)
        );
    }
}
