package com.martinsdev.caretrack.api.service;

import com.martinsdev.caretrack.api.model.Solicitation;
import com.martinsdev.caretrack.api.model.document.SolicitationDocument;
import com.martinsdev.caretrack.api.repository.SolicitationSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SolicitationIndexService {

    private final SolicitationSearchRepository searchRepository;

    public void index(Solicitation solicitation) {
        SolicitationDocument document = SolicitationDocument.builder()
                .id(String.valueOf(solicitation.getId()))
                .clientId(String.valueOf(solicitation.getClient().getId()))
                .status(solicitation.getStatus().name())
                .createdAt(solicitation.getCreatedAt())
                .submittedAt(solicitation.getSubmittedAt())
                // Step 1 - Pode ser null
                .serviceType(solicitation.getStepOneData() != null ? solicitation.getStepOneData().getServiceType().name() : null)
                .title(solicitation.getStepOneData() != null ? solicitation.getStepOneData().getTitle() : null)
                .description(solicitation.getStepOneData() != null ? solicitation.getStepOneData().getDescription() : null)
                //Step 2 - Pode ser null
                .state(solicitation.getStepTwoData() != null ? solicitation.getStepTwoData().getState() : null)
                .city(solicitation.getStepTwoData() != null ? solicitation.getStepTwoData().getCity() : null)
                //Step 3 - Pode ser null
                .priority(solicitation.getStepThreeData() != null ? solicitation.getStepThreeData().getPriority().name() : null)
                .build();
        searchRepository.save(document);
    }
}
