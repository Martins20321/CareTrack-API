package com.martinsdev.solicitation.api.dto;

import com.martinsdev.solicitation.api.model.embedded.StepTwoData;

public record StepTwoDataDTO(String cep,
                             String number,
                             String complement) {
    public StepTwoDataDTO(StepTwoData stepTwoData) {
        this(stepTwoData.getCep(), stepTwoData.getNumber(), stepTwoData.getComplement());
    }
}
