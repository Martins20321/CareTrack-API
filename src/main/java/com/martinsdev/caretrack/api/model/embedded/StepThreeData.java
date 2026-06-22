package com.martinsdev.caretrack.api.model.embedded;

import com.martinsdev.caretrack.api.model.enums.Priority;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class StepThreeData {

    private Priority priority;
    private LocalDate preferredDate;
    private BigDecimal estimatedValue;
    private Boolean termsAccepted;
}
