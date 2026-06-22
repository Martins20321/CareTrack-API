package com.martinsdev.caretrack.api.dto;

import com.martinsdev.caretrack.api.model.embedded.StepOneData;
import com.martinsdev.caretrack.api.model.enums.ServiceType;

public record StepOneDataDTO(ServiceType serviceType,
                             String title,
                             String description){
    public StepOneDataDTO(StepOneData stepOneData) {
        this(stepOneData.getServiceType(), stepOneData.getTitle(), stepOneData.getDescription());
    }
}
