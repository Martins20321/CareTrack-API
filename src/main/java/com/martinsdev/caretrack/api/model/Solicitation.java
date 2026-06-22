package com.martinsdev.caretrack.api.model;

import com.martinsdev.caretrack.api.model.embedded.StepOneData;
import com.martinsdev.caretrack.api.model.embedded.StepThreeData;
import com.martinsdev.caretrack.api.model.embedded.StepTwoData;
import com.martinsdev.caretrack.api.model.enums.StatusSolicitation;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_solicitation")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
public class Solicitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private User client;

    @Enumerated(EnumType.STRING)
    private StatusSolicitation status;
    private Integer currentStep;

    // -- STEPS --
    @Embedded
    private StepOneData stepOneData;
    @Embedded
    private StepTwoData stepTwoData;
    @Embedded
    private StepThreeData stepThreeData;

    // -- AUDIT --
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime submittedAt;
    private LocalDateTime analyzedAt;
    private Long analyzedBy;
    private String analysisComment;

}
