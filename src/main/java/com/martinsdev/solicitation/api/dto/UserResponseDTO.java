package com.martinsdev.solicitation.api.dto;

import java.time.LocalDateTime;

public record UserResponseDTO(Long id,
                              String name,
                              String email,
                              String role,
                              boolean enabled,
                              LocalDateTime createdAt) {
}
