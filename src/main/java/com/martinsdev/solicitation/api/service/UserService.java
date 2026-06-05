package com.martinsdev.solicitation.api.service;

import com.martinsdev.solicitation.api.dto.RegisterRequestDTO;
import com.martinsdev.solicitation.api.infra.exception.EmailAlreadyExistsException;
import com.martinsdev.solicitation.api.model.User;
import com.martinsdev.solicitation.api.model.enums.RoleUser;
import com.martinsdev.solicitation.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User register(RegisterRequestDTO registerDTO) {
        //Verificando se já existe no banco pelo email
        if (repository.existsByEmail(registerDTO.email())){
            throw new EmailAlreadyExistsException(registerDTO.email());
        }

        User user = User.builder()
                        .name(registerDTO.name())
                        .email(registerDTO.email())
                        .passwordHash(passwordEncoder.encode(registerDTO.password()))
                        .role(RoleUser.CLIENT)
                        .enabled(true)
                        .createdAt(LocalDateTime.now())
                        .build();

        repository.save(user);
        return user;
    }
}
