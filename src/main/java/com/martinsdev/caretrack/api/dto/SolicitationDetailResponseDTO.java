package com.martinsdev.caretrack.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.martinsdev.caretrack.api.model.Solicitation;
import com.martinsdev.caretrack.api.model.enums.StatusSolicitation;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SolicitationDetailResponseDTO(Long id,
                                            StatusSolicitation status,
                                            Integer currentStep,
                                            LocalDateTime createdAt,
                                            LocalDateTime updateAt,
                                            LocalDateTime submittedAt,
                                            LocalDateTime analyzedAt,
                                            Long analyzedBy,
                                            String analysisComment,
                                            StepOneDataDTO stepOne,
                                            StepTwoDataDTO stepTwo,
                                            StepThreeDataDTO stepThree) {

    public SolicitationDetailResponseDTO(Solicitation solicitation) {
        this(solicitation.getId(), solicitation.getStatus(), solicitation.getCurrentStep(), solicitation.getCreatedAt(),
                solicitation.getUpdatedAt(), solicitation.getSubmittedAt(), solicitation.getAnalyzedAt(),
                solicitation.getAnalyzedBy(), solicitation.getAnalysisComment(),
                solicitation.getStepOneData() != null ? new StepOneDataDTO(solicitation.getStepOneData()) : null,
                solicitation.getStepTwoData() != null ? new StepTwoDataDTO(solicitation.getStepTwoData()) : null,
                solicitation.getStepThreeData() != null ? new StepThreeDataDTO(solicitation.getStepThreeData()) : null);
    }
}
