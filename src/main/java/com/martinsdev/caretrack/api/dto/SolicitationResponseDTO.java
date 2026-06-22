package com.martinsdev.caretrack.api.dto;

import com.martinsdev.caretrack.api.model.Solicitation;
import com.martinsdev.caretrack.api.model.enums.StatusSolicitation;

import java.time.LocalDateTime;

public record SolicitationResponseDTO(Long id,
                                      StatusSolicitation status,
                                      Integer currentStep,
                                      LocalDateTime createdAt,
                                      LocalDateTime updateAt,
                                      LocalDateTime submittedAt) {

    public SolicitationResponseDTO(Solicitation solicitation) {
        this(solicitation.getId(), solicitation.getStatus(), solicitation.getCurrentStep(), solicitation.getCreatedAt(), solicitation.getUpdatedAt(), solicitation.getSubmittedAt());
    }
}
