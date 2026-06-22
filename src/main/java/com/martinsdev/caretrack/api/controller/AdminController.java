package com.martinsdev.caretrack.api.controller;

import com.martinsdev.caretrack.api.dto.AnalystCoverageResponseDTO;
import com.martinsdev.caretrack.api.dto.CreateUserRequestDTO;
import com.martinsdev.caretrack.api.dto.UpdateCoverageRequestDTO;
import com.martinsdev.caretrack.api.dto.UserResponseDTO;
import com.martinsdev.caretrack.api.infra.aop.Audit;
import com.martinsdev.caretrack.api.service.AdminService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
public class AdminController {

    private final AdminService service;

    @Audit(action = "CREATE_USER")
    @PostMapping("/users")
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody @Valid CreateUserRequestDTO userRequestDTO) {
        UserResponseDTO user = service.createUser(userRequestDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{email}").buildAndExpand(user.email()).toUri();
        return ResponseEntity.created(uri).body(user);
    }

    @PutMapping("/users/{id}/coverage")
    public ResponseEntity<AnalystCoverageResponseDTO> updateCoverage(@PathVariable Long id, @RequestBody @Valid UpdateCoverageRequestDTO coverageRequestDTO){
        AnalystCoverageResponseDTO coverage = service.updateCoverage(id, coverageRequestDTO);
        return ResponseEntity.ok(coverage);
    }
}
