package com.martinsdev.caretrack.api.controller;

import com.martinsdev.caretrack.api.dto.*;
import com.martinsdev.caretrack.api.infra.aop.Audit;
import com.martinsdev.caretrack.api.model.User;
import com.martinsdev.caretrack.api.service.SolicitationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/solicitations")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
public class SolicitationController {

    private final SolicitationService service;

    @GetMapping
    public ResponseEntity<List<SolicitationDetailResponseDTO>> findAllByClient(@AuthenticationPrincipal User client) {
        return ResponseEntity.ok(service.findAllByClient(client));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitationDetailResponseDTO> findById(@PathVariable Long id,@AuthenticationPrincipal User client) {
        return ResponseEntity.ok(service.findById(id, client));
    }

    @PostMapping
    public ResponseEntity<SolicitationResponseDTO> createDraft(@AuthenticationPrincipal User client) {
        SolicitationResponseDTO solicitation = service.create(client);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(solicitation.id()).toUri();
        return ResponseEntity.created(uri).body(solicitation);
    }

    @PutMapping("/{id}/step1")
    public ResponseEntity<SolicitationResponseDTO> saveStep1(@PathVariable Long id, @RequestBody @Valid StepOneRequestDTO oneRequestDTO, @AuthenticationPrincipal User client) {
        SolicitationResponseDTO solicitation1St1 = service.saveStep1(id, oneRequestDTO, client);
        return ResponseEntity.ok(solicitation1St1);
    }

    @PutMapping("/{id}/step2")
    public ResponseEntity<SolicitationResponseDTO> saveStep2(@PathVariable Long id, @RequestBody @Valid StepTwoRequestDTO twoRequestDTO, @AuthenticationPrincipal User client) {
        SolicitationResponseDTO solicitationSt2 = service.saveStep2(id, twoRequestDTO, client);
        return ResponseEntity.ok(solicitationSt2);
    }

    @PutMapping("/{id}/step3")
    public ResponseEntity<SolicitationResponseDTO> saveStep3(@PathVariable Long id, @RequestBody @Valid StepThreeRequestDTO threeRequestDTO, @AuthenticationPrincipal User client) {
        SolicitationResponseDTO solicitationSt3 = service.saveStep3(id, threeRequestDTO, client);
        return ResponseEntity.ok(solicitationSt3);
    }

    @Audit(action = "SUBMIT_SOLICITATION")
    @PostMapping("/{id}/submit")
    public ResponseEntity<SolicitationResponseDTO> submit(@PathVariable Long id, @AuthenticationPrincipal User client) {
        SolicitationResponseDTO solicitationSub = service.submit(id, client);
        return ResponseEntity.ok(solicitationSub);
    }
}
