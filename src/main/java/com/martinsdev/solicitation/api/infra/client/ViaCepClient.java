package com.martinsdev.solicitation.api.infra.client;

import com.martinsdev.solicitation.api.dto.ViaCepResponseDTO;
import com.martinsdev.solicitation.api.infra.exception.ExternalServiceException;
import com.martinsdev.solicitation.api.infra.exception.InvalidCepException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
public class ViaCepClient {

    private final RestClient restClient;

    //URL base da API
    public ViaCepClient() {
        this.restClient = RestClient.create("https://viacep.com.br/ws");
    }

    public ViaCepResponseDTO findAddressByCep(String cep) {
        try{
            ViaCepResponseDTO cepResponseDTO = restClient.get()
                    .uri("/{cep}/json", cep)
                    .retrieve()
                    .body(ViaCepResponseDTO.class);

            //Tratando o nullPointerException
            if (Boolean.TRUE.equals(cepResponseDTO.error())){
                throw new InvalidCepException(cep);
            }

            return cepResponseDTO;
        } catch (Exception e) {
            throw new ExternalServiceException("ViaCEP service unavailable. Please try again later");
        }
    }
}
