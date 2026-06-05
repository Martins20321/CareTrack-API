package com.martinsdev.solicitation.api.dto;

import com.martinsdev.solicitation.api.model.AnalystCoverage;

import java.util.List;

public record AnalystCoverageResponseDTO(Long id,
                                         Long userId,
                                         List<String> states) {

    public AnalystCoverageResponseDTO(AnalystCoverage coverage) {
        this(coverage.getId(), coverage.getUser().getId(), coverage.getStates());
    }
}
