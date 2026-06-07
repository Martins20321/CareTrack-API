package com.martinsdev.solicitation.api.dto;

import com.martinsdev.solicitation.api.model.enums.Priority;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StepThreeRequestDTO(@NotNull(message = "Priority is required") Priority priority,
                                  @NotNull(message = "Preferred Date is required") @Future(message = "Only future dates are accepted") LocalDate preferredDate,
                                  @NotNull(message = "Estimated Value is required") @DecimalMin("0.0") BigDecimal estimatedValue,
                                  @NotNull(message = "Terms Accepted is required") Boolean termsAccepted) {
}
