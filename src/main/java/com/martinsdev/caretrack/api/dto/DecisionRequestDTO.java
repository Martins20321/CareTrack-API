package com.martinsdev.caretrack.api.dto;

import com.martinsdev.caretrack.api.model.enums.Decision;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DecisionRequestDTO(@NotNull Decision decision,
                                 @NotBlank @Size(min = 10, max = 1000) String comment) {
}
