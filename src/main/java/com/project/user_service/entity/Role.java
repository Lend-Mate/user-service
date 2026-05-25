package com.project.user_service.entity;

import java.util.EnumSet;
import java.util.Set;

public enum Role {
    ADMIN(EnumSet.of(
            Permission.USER_READ,
            Permission.USER_WRITE,
            Permission.USER_DELETE,
            Permission.PRODUCT_READ,
            Permission.PRODUCT_WRITE,
            Permission.PRODUCT_DELETE,
            Permission.RENTAL_READ,
            Permission.RENTAL_WRITE,
            Permission.RENTAL_DELETE,
            Permission.ADMIN_PANEL,
            Permission.LENDER
    )),
    LENDER(EnumSet.of(
            Permission.PRODUCT_READ,
            Permission.PRODUCT_WRITE,
            Permission.RENTAL_READ,
            Permission.RENTAL_WRITE,
            Permission.LENDER
    )),
    CUSTOMER(EnumSet.of(
            Permission.PRODUCT_READ,
            Permission.RENTAL_READ,
            Permission.RENTAL_WRITE
    ));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }
}
