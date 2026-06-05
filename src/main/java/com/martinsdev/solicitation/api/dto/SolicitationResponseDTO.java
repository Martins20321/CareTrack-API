package com.martinsdev.solicitation.api.dto;

import com.martinsdev.solicitation.api.model.enums.StatusSolicitation;

import java.time.LocalDateTime;

public record SolicitationResponseDTO(Long id,
                                      StatusSolicitation status,
                                      Integer currentStep,
                                      LocalDateTime createdAt,
                                      LocalDateTime updateAt) {
}
