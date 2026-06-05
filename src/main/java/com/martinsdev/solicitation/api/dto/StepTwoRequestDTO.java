package com.martinsdev.solicitation.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

//Irá ocorrer atualização quando integrar a API do ViaCep
public record StepTwoRequestDTO(@NotBlank(message = "Cep is required") String cep,
                                @NotBlank(message = "Number is required") @Size(max = 20) String number,
                                String complement,
                                @NotBlank(message = "street is required") String street,
                                @NotBlank(message = "Neighborhood is required") String neighborhood,
                                @NotBlank(message = "City is required") String city,
                                @NotBlank(message = "State is required") @Size(min = 2, max = 2) String state) {
}
