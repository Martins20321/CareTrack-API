package com.martinsdev.solicitation.api.dto;

import com.martinsdev.solicitation.api.model.embedded.StepOneData;
import com.martinsdev.solicitation.api.model.enums.ServiceType;

public record StepOneDataDTO(ServiceType serviceType,
                             String title,
                             String description){
    public StepOneDataDTO(StepOneData stepOneData) {
        this(stepOneData.getServiceType(), stepOneData.getTitle(), stepOneData.getDescription());
    }
}
