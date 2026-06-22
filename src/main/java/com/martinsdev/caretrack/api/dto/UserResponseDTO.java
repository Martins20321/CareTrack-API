package com.martinsdev.caretrack.api.dto;

import com.martinsdev.caretrack.api.model.User;
import com.martinsdev.caretrack.api.model.enums.RoleUser;

import java.time.LocalDateTime;

public record UserResponseDTO(Long id,
                              String name,
                              String email,
                              RoleUser role,
                              boolean enabled,
                              LocalDateTime createdAt) {

    public UserResponseDTO(User user) {
        this(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.isEnabled(), user.getCreatedAt());
    }
}
