package com.martinsdev.solicitation.api.service;

import com.martinsdev.solicitation.api.dto.*;
import com.martinsdev.solicitation.api.infra.client.ViaCepClient;
import com.martinsdev.solicitation.api.infra.exception.InvalidOperationException;
import com.martinsdev.solicitation.api.infra.exception.ResourceNotFoundException;
import com.martinsdev.solicitation.api.infra.exception.UnauthorizedException;
import com.martinsdev.solicitation.api.model.Solicitation;
import com.martinsdev.solicitation.api.model.User;
import com.martinsdev.solicitation.api.model.embedded.StepOneData;
import com.martinsdev.solicitation.api.model.embedded.StepThreeData;
import com.martinsdev.solicitation.api.model.embedded.StepTwoData;
import com.martinsdev.solicitation.api.model.enums.Priority;
import com.martinsdev.solicitation.api.model.enums.StatusSolicitation;
import com.martinsdev.solicitation.api.repository.SolicitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SolicitationService {

    private final SolicitationRepository repository;
    private final ViaCepClient cepClient;

    //Criação de rascunho/DRAFT
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

    //Atualização da solicitação no step1
    @Transactional
    public SolicitationResponseDTO saveStep1(Long id, StepOneRequestDTO oneRequestDTO, User client) {
        //Buscando a solicitação no banco
        Solicitation solicitationSt1 = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitation not found by id: " + id));

        //Verificando se é o mesmo cliente
        if (!solicitationSt1.getClient().getId().equals(client.getId())) {
            throw new UnauthorizedException();
        }

        //Verificando se está com status DRAFT/Step anterior
        if (solicitationSt1.getStatus() != StatusSolicitation.DRAFT) {
            throw new InvalidOperationException("Solicitation can only be edited when status is DRAFT");
        }

        StepOneData stepOne = StepOneData.builder()
                .serviceType(oneRequestDTO.serviceType())
                .title(oneRequestDTO.title())
                .description(oneRequestDTO.description())
                .build();

        solicitationSt1.setStepOneData(stepOne);
        solicitationSt1.setCurrentStep(1);
        solicitationSt1.setUpdatedAt(LocalDateTime.now());

        repository.save(solicitationSt1);
        return new SolicitationResponseDTO(solicitationSt1);
    }

    //Atualização da solicitação com step2
    @Transactional
    public SolicitationResponseDTO saveStep2(Long id, StepTwoRequestDTO twoRequestDTO, User client) {
        //Buscando solicitação no banco
        Solicitation solicitation = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitation not found by id: " + id));

        //Verificando se é o mesmo cliente
        if (!solicitation.getClient().getId().equals(client.getId())) {
            throw new UnauthorizedException();
        }

        if (solicitation.getStatus() != StatusSolicitation.DRAFT) {
            throw new InvalidOperationException("Solicitation can only be edited when status is DRAFT");
        }

        //O ViaCep devolve os campos
        ViaCepResponseDTO addressByCep = cepClient.findAddressByCep(twoRequestDTO.cep());

        StepTwoData stepTwo = StepTwoData.builder()
                .cep(addressByCep.cep())
                .number(twoRequestDTO.number())
                .complement(twoRequestDTO.complement())
                .street(addressByCep.street())
                .neighborhood(addressByCep.neighborhood())
                .city(addressByCep.city())
                .state(addressByCep.state())
                .build();

        solicitation.setStepTwoData(stepTwo);
        solicitation.setCurrentStep(2);
        solicitation.setUpdatedAt(LocalDateTime.now());

        repository.save(solicitation);
        return new SolicitationResponseDTO(solicitation);
    }

    //Atualização da solicitação com step3
    @Transactional
    public SolicitationResponseDTO saveStep3(Long id, StepThreeRequestDTO threeRequestDTO, User client) {
        //Buscando solicitação no banco
        Solicitation solicitationSt3 = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitation not found by id: " + id));

        if (!solicitationSt3.getClient().getId().equals(client.getId())) {
            throw new UnauthorizedException();
        }

        if (solicitationSt3.getStatus() != StatusSolicitation.DRAFT) {
            throw new InvalidOperationException("Solicitation can only be edited when status is DRAFT");
        }

        StepThreeData stepThree = StepThreeData.builder()
                .priority(threeRequestDTO.priority())
                .preferredDate(threeRequestDTO.preferredDate())
                .estimatedValue(threeRequestDTO.estimatedValue())
                .termsAccepted(threeRequestDTO.termsAccepted())
                .build();

        if (stepThree.getPriority() == Priority.HIGH && stepThree.getEstimatedValue().compareTo(BigDecimal.valueOf(100)) < 0) {
            throw new InvalidOperationException("If the priority is High, the estimated minimum value is 100.0!");
        }

        solicitationSt3.setStepThreeData(stepThree);
        solicitationSt3.setCurrentStep(3);
        solicitationSt3.setUpdatedAt(LocalDateTime.now());

        repository.save(solicitationSt3);
        return new SolicitationResponseDTO(solicitationSt3);
    }
}
