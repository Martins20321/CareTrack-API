package com.martinsdev.solicitation.api.repository;

import com.martinsdev.solicitation.api.model.AnalystCoverage;
import com.martinsdev.solicitation.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnalystCoverageRepository extends JpaRepository<AnalystCoverage, Long> {

    Optional<AnalystCoverage> findByUser(User user);
}
