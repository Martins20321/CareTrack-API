package com.martinsdev.solicitation.api.service;

import com.martinsdev.solicitation.api.dto.SolicitationResponseDTO;
import com.martinsdev.solicitation.api.dto.StepOneRequestDTO;
import com.martinsdev.solicitation.api.infra.exception.InvalidOperationException;
import com.martinsdev.solicitation.api.infra.exception.ResourceNotFoundException;
import com.martinsdev.solicitation.api.infra.exception.UnauthorizedException;
import com.martinsdev.solicitation.api.model.Solicitation;
import com.martinsdev.solicitation.api.model.User;
import com.martinsdev.solicitation.api.model.embedded.StepOneData;
import com.martinsdev.solicitation.api.model.enums.StatusSolicitation;
import com.martinsdev.solicitation.api.repository.SolicitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SolicitationService {

    private final SolicitationRepository repository;

    //Criação de rescunho/DRAFT
    @Transactional
    public SolicitationResponseDTO create(User client) {
        Solicitation solicitation = Solicitation.builder()
                .status(StatusSolicitation.DRAFT)
                .currentStep(1)
                .client(client)
                .createdAt(LocalDateTime.now())
                .build();
        repository.save(solicitation);
        return new SolicitationResponseDTO(solicitation);
    }

    @Transactional
    public SolicitationResponseDTO saveStep1(Long id, StepOneRequestDTO oneRequestDTO, User client) {
        //Buscando a solicitação no banco
        Solicitation solicitationSt1 = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitation not found by id: " + id));

        //Verificando se é o mesmo cliente
        if (!solicitationSt1.getClient().getId().equals(client.getId())){
            throw new UnauthorizedException();
        }

        //Verificando se está com status DRAFT
        if (solicitationSt1.getStatus() != StatusSolicitation.DRAFT){
            throw new InvalidOperationException("Solicitation can only be edited when status is DRAFT");
        }

        StepOneData stepOne = StepOneData.builder()
                .serviceType(oneRequestDTO.serviceType())
                .title(oneRequestDTO.title())
                .description(oneRequestDTO.description())
                .build();

        solicitationSt1.setStepOneData(stepOne);
        solicitationSt1.setCurrentStep(1);

        repository.save(solicitationSt1);
        return new SolicitationResponseDTO(solicitationSt1);
    }
}
