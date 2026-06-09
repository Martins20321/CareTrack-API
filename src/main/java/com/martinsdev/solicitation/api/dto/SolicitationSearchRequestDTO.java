package com.martinsdev.solicitation.api.dto;

import java.time.LocalDateTime;

public record SolicitationSearchRequestDTO(String q,
                                           String status,
                                           String serviceType,
                                           String priority,
                                           String state,
                                           LocalDateTime dateFrom,
                                           LocalDateTime dateTo,
                                           int page,
                                           int size) {
}
