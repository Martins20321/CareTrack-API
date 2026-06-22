package com.martinsdev.caretrack.api.repository;

import com.martinsdev.caretrack.api.model.User;
import com.martinsdev.caretrack.api.model.enums.RoleUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByRole(RoleUser role);
}
