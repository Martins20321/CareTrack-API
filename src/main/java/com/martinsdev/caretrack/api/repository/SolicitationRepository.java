package com.martinsdev.caretrack.api.repository;

import com.martinsdev.caretrack.api.model.Solicitation;
import com.martinsdev.caretrack.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitationRepository extends JpaRepository<Solicitation, Long> {

    List<Solicitation> findByStepTwoData_StateIn(List<String> states);

    List<Solicitation> findByClient(User client);

}
