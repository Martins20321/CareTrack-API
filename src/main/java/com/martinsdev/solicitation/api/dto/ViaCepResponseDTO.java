package com.martinsdev.solicitation.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ViaCepResponseDTO(String cep,
                                @JsonProperty("logradouro") String street,
                                @JsonProperty("complemento") String complement,
                                @JsonProperty("bairro") String neighborhood,
                                @JsonProperty("localidade") String city,
                                @JsonProperty("uf") String state,
                                Boolean error) {
}
