package com.project.user_service.entity;

public enum Permission {
    // Kullanıcı kendi verilerine erişir
    PROFILE_READ,
    PROFILE_WRITE,
    PROFILE_DELETE,

    // Admin tüm kullanıcıları yönetir
    USER_READ,
    USER_WRITE,
    USER_DELETE,

    // Kiralama
    RENTAL_READ,
    RENTAL_WRITE,
    RENTAL_DELETE,

    // Admin paneli
    ADMIN_PANEL
}