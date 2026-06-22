package com.martinsdev.caretrack.api.infra.security;

import com.martinsdev.caretrack.api.model.User;
import com.martinsdev.caretrack.api.model.enums.RoleUser;
import com.martinsdev.caretrack.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Profile("!test")
public class DataSeeder implements ApplicationRunner {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (repository.findByRole(RoleUser.ADMIN).isEmpty()){
            User user = User.builder()
                    .name("adminSolicitation")
                    .email("admin@solicitation.com")
                    .passwordHash(passwordEncoder.encode("admin123456"))
                    .role(RoleUser.ADMIN)
                    .enabled(true)
                    .createdAt(LocalDateTime.now())
                    .build();
            repository.save(user);
        }
    }
}
