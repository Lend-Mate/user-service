package com.lendmate.userservice.entity;

import java.util.EnumSet;
import java.util.Set;

public enum Role {
    ADMIN(EnumSet.of(
            Permission.PROFILE_READ,
            Permission.PROFILE_WRITE,
            Permission.PROFILE_DELETE,
            Permission.USER_READ,
            Permission.USER_WRITE,
            Permission.USER_DELETE,
            Permission.RENTAL_READ,
            Permission.RENTAL_WRITE,
            Permission.RENTAL_DELETE,
            Permission.ADMIN_PANEL
    )),
    USER(EnumSet.of(
            Permission.PROFILE_READ,
            Permission.PROFILE_WRITE,
            Permission.PROFILE_DELETE,
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