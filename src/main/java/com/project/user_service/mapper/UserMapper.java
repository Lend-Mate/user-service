package com.project.user_service.mapper;

import com.project.user_service.entity.User;
import com.project.user_service.dto.requestDto.UserRequest; // Bu paket adını projenize göre güncelleyebilirsiniz
import com.project.user_service.dto.responseDto.UserResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@AllArgsConstructor
public class UserMapper {

    // Eğer Spring Security kullanıyorsanız şifreyi kaydederken encode etmek için ekleyebilirsiniz.
    // Kullanmıyorsanız bu satırı ve toEntity/updateEntity içindeki encode kısımlarını kaldırabilirsiniz.
    // private final PasswordEncoder passwordEncoder;

    public User toEntity(UserRequest userRequest) {
        if (userRequest == null) return null;

        User user = new User();
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setUsername(userRequest.getUsername());

        // Şifreyi açık metin (raw) kaydetmemek için encode ediyoruz
        // user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setPassword(userRequest.getPassword());

        user.setRole(userRequest.getRole() != null ? userRequest.getRole() : "USER"); // Varsayılan rol
        user.setPhone(userRequest.getPhone());
        user.setEmail(userRequest.getEmail());
        user.setProfileImage(userRequest.getProfileImage());
        user.setVerified(false); // Yeni kayıt olan kullanıcı varsayılan olarak doğrulanmamış olabilir
        user.setDeleted(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return user;
    }

    public UserResponse toDto(User user) {
        if (user == null) return null;

        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .profileImage(user.getProfileImage())
                .verified(user.isVerified())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public void updateEntity(User user, UserRequest request) {
        if (user == null || request == null) return;

        if (request.getFirstName() != null)    user.setFirstName(request.getFirstName());
        if (request.getLastName() != null)     user.setLastName(request.getLastName());
        if (request.getUsername() != null)     user.setUsername(request.getUsername());
        if (request.getPhone() != null)        user.setPhone(request.getPhone());
        if (request.getEmail() != null)        user.setEmail(request.getEmail());
        if (request.getRole() != null)         user.setRole(request.getRole());
        if (request.getProfileImage() != null) user.setProfileImage(request.getProfileImage());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            // user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setPassword(request.getPassword());
        }

        user.setUpdatedAt(LocalDateTime.now());
    }
}