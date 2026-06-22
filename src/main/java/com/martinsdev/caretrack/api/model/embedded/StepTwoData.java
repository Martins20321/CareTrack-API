package com.martinsdev.caretrack.api.model.embedded;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class StepTwoData {

    private String cep;
    private String number;
    private String complement;
    private String street;
    private String neighborhood;
    private String city;
    private String state;
}
