package com.martinsdev.solicitation.api.controller;

import com.martinsdev.solicitation.api.dto.DecisionRequestDTO;
import com.martinsdev.solicitation.api.dto.SolicitationResponseDTO;
import com.martinsdev.solicitation.api.model.User;
import com.martinsdev.solicitation.api.service.AnalystService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/analyst/solicitations")
@RequiredArgsConstructor
public class AnalystController {

    private final AnalystService service;

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

    @PostMapping("/{id}/decide")
    public ResponseEntity<SolicitationResponseDTO> decide(@PathVariable Long id, @RequestBody @Valid DecisionRequestDTO decisionRequestDTO, @AuthenticationPrincipal User analyst) {
        return ResponseEntity.ok(service.decide(id, decisionRequestDTO, analyst));
    }
}
