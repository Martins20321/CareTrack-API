package com.martinsdev.solicitation.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateCoverageRequestDTO(@NotNull(message = "States list is required")
                                       @Size(min = 1, message = "At least one states is required")  List<String> states) {
}
