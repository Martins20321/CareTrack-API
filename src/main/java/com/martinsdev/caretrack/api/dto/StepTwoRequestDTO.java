package com.martinsdev.caretrack.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StepTwoRequestDTO(@NotBlank(message = "Cep is required") String cep,
                                @NotBlank(message = "Number is required") @Size(max = 20) String number,
                                String complement) {
}
