package com.martinsdev.solicitation.api.service;

import com.martinsdev.solicitation.api.dto.DecisionRequestDTO;
import com.martinsdev.solicitation.api.dto.SolicitationResponseDTO;
import com.martinsdev.solicitation.api.infra.exception.InvalidOperationException;
import com.martinsdev.solicitation.api.infra.exception.ResourceNotFoundException;
import com.martinsdev.solicitation.api.infra.exception.UnauthorizedException;
import com.martinsdev.solicitation.api.model.AnalystCoverage;
import com.martinsdev.solicitation.api.model.Solicitation;
import com.martinsdev.solicitation.api.model.User;
import com.martinsdev.solicitation.api.model.enums.Decision;
import com.martinsdev.solicitation.api.model.enums.StatusSolicitation;
import com.martinsdev.solicitation.api.repository.AnalystCoverageRepository;
import com.martinsdev.solicitation.api.repository.SolicitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalystService {

    private final AnalystCoverageRepository repository;
    private final SolicitationRepository solicitationRepository;
    private final SolicitationIndexService solicitationIndexService;

    public List<SolicitationResponseDTO> getSolicitations(User analyst) {
        //Busca o analyst no banco pelo usuário
        AnalystCoverage coverage = repository.findByUser(analyst)
                .orElseThrow(() -> new ResourceNotFoundException("Analyst not found"));

        //Pega a lista de estados do analyst
        List<String> states = coverage.getStates();

        //Pega as solicitações que estão dentro da lista de estados que o analista abrange
        List<Solicitation> solicitationList = solicitationRepository.findByStepTwoData_StateIn(states);

        return solicitationList.stream().map(SolicitationResponseDTO::new).toList();
    }

    public SolicitationResponseDTO getSolicitationById(Long id, User analyst) {
        Solicitation solicitation = solicitationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitation not found by id: " + id));

        AnalystCoverage coverage = repository.findByUser(analyst)
                .orElseThrow(() -> new ResourceNotFoundException("Analyst not found"));

        if (!coverage.getStates().contains(solicitation.getStepTwoData().getState())){
            throw new UnauthorizedException();
        }

        return new SolicitationResponseDTO(solicitation);
    }

    public SolicitationResponseDTO start(Long id, User analyst) {
        //Buscando solicitação
        Solicitation solicitation = solicitationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitation not found by id: " + id));

        AnalystCoverage coverage = repository.findByUser(analyst)
                .orElseThrow(() -> new ResourceNotFoundException("Analyst not found"));

        //Verifica se o state está no coverage do analyst
        if (!coverage.getStates().contains(solicitation.getStepTwoData().getState())) {
            throw new UnauthorizedException();
        }

        if (solicitation.getStatus() != StatusSolicitation.SUBMITTED) {
            throw new InvalidOperationException("This operation can only be performed with the status 'SUBMITTED'");
        }

        solicitation.setStatus(StatusSolicitation.IN_REVIEW);
        solicitation.setUpdatedAt(LocalDateTime.now());

        solicitationRepository.save(solicitation);
        solicitationIndexService.index(solicitation);

        return new SolicitationResponseDTO(solicitation);
    }

    public SolicitationResponseDTO decide(Long id, DecisionRequestDTO decisionRequestDTO, User analyst) {
        //Buscar solicitação por Id
        Solicitation solicitation = solicitationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitation not found by id: " + id));

        AnalystCoverage coverage = repository.findByUser(analyst)
                .orElseThrow(() -> new ResourceNotFoundException("Analyst not found"));

        if (!coverage.getStates().contains(solicitation.getStepTwoData().getState())) {
            throw new UnauthorizedException();
        }

        if (solicitation.getStatus() != StatusSolicitation.SUBMITTED && solicitation.getStatus() != StatusSolicitation.IN_REVIEW) {
            throw new InvalidOperationException("You can only decide if the status is `SUBMITTED` or `IN_REVIEW`");
        }

        if (decisionRequestDTO.decision() == Decision.APPROVE) {
            solicitation.setStatus(StatusSolicitation.APPROVED);
        } else {
            solicitation.setStatus(StatusSolicitation.REJECTED);
        }

        solicitation.setAnalyzedAt(LocalDateTime.now());
        solicitation.setAnalyzedBy(analyst.getId());
        solicitation.setAnalysisComment(decisionRequestDTO.comment());

        solicitationRepository.save(solicitation);
        solicitationIndexService.index(solicitation);

        return new SolicitationResponseDTO(solicitation);
    }


}
