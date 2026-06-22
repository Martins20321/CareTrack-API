package com.martinsdev.caretrack.api.dto;

import com.martinsdev.caretrack.api.model.embedded.StepThreeData;
import com.martinsdev.caretrack.api.model.enums.Priority;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StepThreeDataDTO(Priority priority,
                               LocalDate preferredDate,
                               BigDecimal estimatedValue,
                               Boolean termsAccepted){
    public StepThreeDataDTO(StepThreeData stepThreeData) {
        this(stepThreeData.getPriority(), stepThreeData.getPreferredDate(), stepThreeData.getEstimatedValue(), stepThreeData.getTermsAccepted());
    }
}
