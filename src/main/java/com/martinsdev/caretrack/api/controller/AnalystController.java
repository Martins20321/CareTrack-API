package com.martinsdev.caretrack.api.controller;

import com.martinsdev.caretrack.api.dto.DecisionRequestDTO;
import com.martinsdev.caretrack.api.dto.SolicitationResponseDTO;
import com.martinsdev.caretrack.api.dto.SolicitationSearchRequestDTO;
import com.martinsdev.caretrack.api.infra.aop.Audit;
import com.martinsdev.caretrack.api.model.User;
import com.martinsdev.caretrack.api.model.document.SolicitationDocument;
import com.martinsdev.caretrack.api.service.AnalystService;
import com.martinsdev.caretrack.api.service.SolicitationSearchService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/analyst/solicitations")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
public class AnalystController {

    private final AnalystService service;
    private final SolicitationSearchService searchService;

    @GetMapping
    public ResponseEntity<List<SolicitationResponseDTO>> getSolicitations(@AuthenticationPrincipal User analyst) {
        return ResponseEntity.ok(service.getSolicitations(analyst));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitationResponseDTO> getSolicitationById(@PathVariable Long id, @AuthenticationPrincipal User analyst) {
        return ResponseEntity.ok(service.getSolicitationById(id, analyst));
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<SolicitationResponseDTO> start(@PathVariable Long id, @AuthenticationPrincipal User analyst) {
        return ResponseEntity.ok(service.start(id, analyst));
    }

    @Audit(action = "DECIDE_SOLICITATION")
    @PostMapping("/{id}/decide")
    public ResponseEntity<SolicitationResponseDTO> decide(@PathVariable Long id, @RequestBody @Valid DecisionRequestDTO decisionRequestDTO, @AuthenticationPrincipal User analyst) {
        return ResponseEntity.ok(service.decide(id, decisionRequestDTO, analyst));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<SolicitationDocument>> search(@ModelAttribute SolicitationSearchRequestDTO searchRequestDTO, @AuthenticationPrincipal User client) {
        return ResponseEntity.ok(searchService.search(searchRequestDTO,client));
    }
}
