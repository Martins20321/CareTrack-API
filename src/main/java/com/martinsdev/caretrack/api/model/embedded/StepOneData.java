package com.martinsdev.caretrack.api.model.embedded;

import com.martinsdev.caretrack.api.model.enums.ServiceType;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class StepOneData {

    private ServiceType serviceType;
    private String title;
    private String description;
}
