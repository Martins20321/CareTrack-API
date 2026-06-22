package com.martinsdev.caretrack.api.dto;

import com.martinsdev.caretrack.api.model.enums.ServiceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StepOneRequestDTO(@NotNull(message = "Service type is required") ServiceType serviceType,
                                @NotBlank(message = "Title is required") @Size(min = 3, max = 80) String title,
                                @NotBlank(message = "Description is required") @Size(min = 20, max = 1000) String description) {
}
