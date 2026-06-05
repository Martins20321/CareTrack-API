package com.martinsdev.solicitation.api.model.embedded;

import com.martinsdev.solicitation.api.model.enums.ServiceType;
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
