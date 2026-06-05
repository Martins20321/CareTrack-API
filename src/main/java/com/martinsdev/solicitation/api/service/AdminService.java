package com.martinsdev.solicitation.api.service;

import com.martinsdev.solicitation.api.dto.AnalystCoverageResponseDTO;
import com.martinsdev.solicitation.api.dto.CreateUserRequestDTO;
import com.martinsdev.solicitation.api.dto.UpdateCoverageRequestDTO;
import com.martinsdev.solicitation.api.dto.UserResponseDTO;
import com.martinsdev.solicitation.api.infra.exception.EmailAlreadyExistsException;
import com.martinsdev.solicitation.api.infra.exception.InvalidOperationException;
import com.martinsdev.solicitation.api.infra.exception.ResourceNotFoundException;
import com.martinsdev.solicitation.api.model.AnalystCoverage;
import com.martinsdev.solicitation.api.model.User;
import com.martinsdev.solicitation.api.model.enums.RoleUser;
import com.martinsdev.solicitation.api.repository.AnalystCoverageRepository;
import com.martinsdev.solicitation.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final AnalystCoverageRepository coverageRepository;

    @Transactional
    public UserResponseDTO createUser(CreateUserRequestDTO userRequestDTO) {
        //Verificando se já existe no banco pelo email
        if (repository.existsByEmail(userRequestDTO.email())) {
            throw new EmailAlreadyExistsException(userRequestDTO.email());
        }

        User user = User.builder()
                .name(userRequestDTO.name())
                .email(userRequestDTO.email())
                .passwordHash(passwordEncoder.encode(userRequestDTO.password()))
                .role(userRequestDTO.role())
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build();
        repository.save(user);

        if (userRequestDTO.role() == RoleUser.ANALYST) {
            AnalystCoverage coverage = AnalystCoverage.builder()
                    .user(user)
                    .states(new ArrayList<>())
                    .build();
            coverageRepository.save(coverage);
        }

        return new UserResponseDTO(user);
    }

    @Transactional
    public AnalystCoverageResponseDTO updateCoverage(Long userId, UpdateCoverageRequestDTO coverageRequestDTO) {
        //Buscar o usuário pelo ID
        User user = repository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found by id: " + userId));

        if (user.getRole() != RoleUser.ANALYST){
            throw new InvalidOperationException("This operation can only be performed if the user is an ANALYST");
        }

        //Se for analista, consulta no banco
        AnalystCoverage coverage = coverageRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Coverage not found"));

        coverage.setStates(coverageRequestDTO.states());
        coverageRepository.save(coverage);
        return new AnalystCoverageResponseDTO(coverage);
    }
}
