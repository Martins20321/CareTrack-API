package com.martinsdev.solicitation.api.controller;

import com.martinsdev.solicitation.api.dto.SolicitationResponseDTO;
import com.martinsdev.solicitation.api.dto.StepOneRequestDTO;
import com.martinsdev.solicitation.api.dto.StepThreeRequestDTO;
import com.martinsdev.solicitation.api.dto.StepTwoRequestDTO;
import com.martinsdev.solicitation.api.model.User;
import com.martinsdev.solicitation.api.service.SolicitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/solicitations")
@RequiredArgsConstructor
public class SolicitationController {

    private final SolicitationService service;

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
    public ResponseEntity<SolicitationResponseDTO> saveStep3(@PathVariable Long id, @RequestBody @Valid StepThreeRequestDTO threeRequestDTO, @AuthenticationPrincipal User client){
        SolicitationResponseDTO solicitationSt3 = service.saveStep3(id, threeRequestDTO, client);
        return ResponseEntity.ok(solicitationSt3);
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<SolicitationResponseDTO> submit(@PathVariable Long id, @AuthenticationPrincipal User client){
        SolicitationResponseDTO solicitationSub = service.submit(id,client);
        return ResponseEntity.ok(solicitationSub);
    }
}
