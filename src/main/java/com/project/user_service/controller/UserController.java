package com.project.user_service.controller;

import com.project.user_service.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<String> getProfile() {
        // JWT'den userId çekip kendi profilini döner
        return ResponseEntity.ok("Kullanıcı profili");
    }

    @PutMapping("/profile")
    public ResponseEntity<String> updateProfile(@RequestBody Object updateRequest) {
        // Kendi profilini günceller
        return ResponseEntity.ok("Profil güncellendi");
    }

    @DeleteMapping("/profile")
    public ResponseEntity<String> deleteOwnAccount() {
        return ResponseEntity.ok("Hesap silindi");
    }

    @GetMapping("/rentals")
    public ResponseEntity<String> getMyRentals() {
        // Kendi kiralama geçmişini görür
        // Rental service'e istek atılacak
        return ResponseEntity.ok("Kiralama geçmişi");
    }

    // ==================== ADMIN ENDPOINTS ====================

    @GetMapping("/admin/users")
    public ResponseEntity<String> getAllUsers() {
        // Tüm kullanıcıları listeler
        return ResponseEntity.ok("Tüm kullanıcılar");
    }

    @GetMapping("/admin/users/{id}")
    public ResponseEntity<String> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok("Kullanıcı detayı: " + id);
    }

    @DeleteMapping("/admin/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("Kullanıcı silindi: " + id);
    }

    @PutMapping("/admin/users/{id}/role")
    public ResponseEntity<String> updateUserRole(@PathVariable Long id,
                                                 @RequestBody Object roleRequest) {
        // ROLE_USER → ROLE_ADMIN gibi
        return ResponseEntity.ok("Rol güncellendi: " + id);
    }
}