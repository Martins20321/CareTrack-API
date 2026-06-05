package com.martinsdev.solicitation.api.service;

import com.martinsdev.solicitation.api.dto.SolicitationResponseDTO;
import com.martinsdev.solicitation.api.model.Solicitation;
import com.martinsdev.solicitation.api.model.User;
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
    public SolicitationResponseDTO create(User client){
        Solicitation solicitation = Solicitation.builder()
                .status(StatusSolicitation.DRAFT)
                .currentStep(1)
                .client(client)
                .createdAt(LocalDateTime.now())
                .build();
        repository.save(solicitation);
        return new SolicitationResponseDTO(solicitation);
    }
}
