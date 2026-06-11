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
import java.util.List;

@Service
@RequiredArgsConstructor
public class SolicitationService {

    private final SolicitationRepository repository;
    private final ViaCepClient cepClient;
    private final SolicitationIndexService solicitationIndexService;

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
        solicitationIndexService.index(solicitation);

        return new SolicitationResponseDTO(solicitation);
    }

    public List<SolicitationDetailResponseDTO> findAllByClient(User client) {
        return repository.findByClient(client)
                .stream().map(SolicitationDetailResponseDTO::new).toList();
    }

    public SolicitationDetailResponseDTO findById(Long id, User client) {
         Solicitation solicitation = repository.findById(id)
                 .orElseThrow(() -> new ResourceNotFoundException("Solicitation not found by id: " + id));

         if (!solicitation.getClient().equals(solicitation.getClient().getId())){
             throw new UnauthorizedException();
         }

         return new SolicitationDetailResponseDTO(solicitation);
    }

    //Atualização da solicitação no step1
    @Transactional
    public SolicitationResponseDTO saveStep1(Long id, StepOneRequestDTO oneRequestDTO, User client) {

        //Método contendo todas as verificações
        Solicitation solicitationSt1 = findAndValidateDraftOwnership(id, client);

        StepOneData stepOne = StepOneData.builder()
                .serviceType(oneRequestDTO.serviceType())
                .title(oneRequestDTO.title())
                .description(oneRequestDTO.description())
                .build();

        solicitationSt1.setStepOneData(stepOne);
        solicitationSt1.setCurrentStep(1);
        solicitationSt1.setUpdatedAt(LocalDateTime.now());

        repository.save(solicitationSt1);
        solicitationIndexService.index(solicitationSt1);

        return new SolicitationResponseDTO(solicitationSt1);
    }

    //Atualização da solicitação com step2
    @Transactional
    public SolicitationResponseDTO saveStep2(Long id, StepTwoRequestDTO twoRequestDTO, User client) {

        //Método contendo todas as verificações
        Solicitation solicitationSt2 = findAndValidateDraftOwnership(id, client);

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

        solicitationSt2.setStepTwoData(stepTwo);
        solicitationSt2.setCurrentStep(2);
        solicitationSt2.setUpdatedAt(LocalDateTime.now());

        repository.save(solicitationSt2);
        solicitationIndexService.index(solicitationSt2);

        return new SolicitationResponseDTO(solicitationSt2);
    }

    //Atualização da solicitação com step3
    @Transactional
    public SolicitationResponseDTO saveStep3(Long id, StepThreeRequestDTO threeRequestDTO, User client) {

        //Método contendo todas as verificações
        Solicitation solicitationSt3 = findAndValidateDraftOwnership(id, client);

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
        solicitationIndexService.index(solicitationSt3);

        return new SolicitationResponseDTO(solicitationSt3);
    }

    //Enviando para análise(Submitted)
    @Transactional
    public SolicitationResponseDTO submit(Long id, User client) {

        //Método contendo todas as verificações
        Solicitation solicitationSub = findAndValidateDraftOwnership(id, client);

        //Verificação dos campos necessários, com validação para os atributos essenciais, evitando o hibernate entregar um objeto com campos nulos
        validateStepsComplete(solicitationSub);

        solicitationSub.setStatus(StatusSolicitation.SUBMITTED); //Não pode mais alterar
        solicitationSub.setUpdatedAt(LocalDateTime.now());
        solicitationSub.setSubmittedAt(LocalDateTime.now());

        repository.save(solicitationSub);
        solicitationIndexService.index(solicitationSub);

        return new SolicitationResponseDTO(solicitationSub);
    }

    private Solicitation findAndValidateDraftOwnership(Long id, User client) {
        //Buscando a solicitação no banco
        Solicitation solicitation = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitation not found by id: " + id));

        //Verificando se é o mesmo cliente
        if (!solicitation.getClient().getId().equals(client.getId())) {
            throw new UnauthorizedException();
        }

        //Verificando se está com status DRAFT/Step anterior
        if (solicitation.getStatus() != StatusSolicitation.DRAFT) {
            throw new InvalidOperationException("Solicitation can only be edited when status is DRAFT");
        }

        return solicitation;
    }

    private void validateStepsComplete(Solicitation solicitation) {
        if (solicitation.getStepOneData() == null || solicitation.getStepOneData().getServiceType() == null) {
            throw new InvalidOperationException("Step 1 is not complete");
        }

        if (solicitation.getStepTwoData() == null || solicitation.getStepTwoData().getCep() == null) {
            throw new InvalidOperationException("Step 2 is not complete");
        }

        if (solicitation.getStepThreeData() == null || solicitation.getStepThreeData().getPriority() == null) {
            throw new InvalidOperationException("Step 3 is not complete");
        }

        if (!solicitation.getStepThreeData().getTermsAccepted()) {
            throw new InvalidOperationException("Terms must be accepted to submit the solicitation");
        }
    }
}
