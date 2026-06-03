package com.martinsdev.solicitation.api.model.embedded;

import com.martinsdev.solicitation.api.model.enums.ServiceType;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StepOneData {

    private ServiceType serviceType;
    private String title;
    private String description;
}
