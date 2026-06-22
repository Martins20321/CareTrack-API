package com.martinsdev.caretrack.api.service;

import com.martinsdev.caretrack.api.dto.SolicitationResponseDTO;
import com.martinsdev.caretrack.api.infra.client.ViaCepClient;
import com.martinsdev.caretrack.api.infra.exception.InvalidOperationException;
import com.martinsdev.caretrack.api.infra.exception.UnauthorizedException;
import com.martinsdev.caretrack.api.model.Solicitation;
import com.martinsdev.caretrack.api.model.User;
import com.martinsdev.caretrack.api.model.embedded.StepOneData;
import com.martinsdev.caretrack.api.model.embedded.StepThreeData;
import com.martinsdev.caretrack.api.model.embedded.StepTwoData;
import com.martinsdev.caretrack.api.model.enums.Priority;
import com.martinsdev.caretrack.api.model.enums.RoleUser;
import com.martinsdev.caretrack.api.model.enums.ServiceType;
import com.martinsdev.caretrack.api.model.enums.StatusSolicitation;
import com.martinsdev.caretrack.api.repository.SolicitationRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SolicitationServiceTest {

    @InjectMocks
    private SolicitationService service;

    @Mock
    private SolicitationRepository repository;

    @Mock
    private ViaCepClient cepClient;

    @Mock
    private SolicitationIndexService solicitationIndexService;

    private User client;
    private User client2;
    private Solicitation solicitation;

    @BeforeEach
    void initialization() {
        client = User.builder()
                .id(1L)
                .name("Pedro Roberto")
                .email("pedro@email.com")
                .passwordHash("12345")
                .role(RoleUser.CLIENT)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build();

        client2 = User.builder()
                .id(2l)
                .name("Guilherme Silva")
                .email("guilherme@email.com")
                .passwordHash("9090")
                .role(RoleUser.CLIENT)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build();

        StepOneData stepOneData = StepOneData.builder()
                .serviceType(ServiceType.INSTALLATION)
                .title("Instalação de equipamento")
                .description("Preciso de instalação de equipamento industrial na minha empresa")
                .build();
        StepTwoData stepTwoData = StepTwoData.builder()
                .cep("01001000")
                .number("10A")
                .street("Praça de Sá")
                .neighborhood("Sé")
                .city("São Paulo")
                .state("SP")
                .build();
        StepThreeData stepThreeData = StepThreeData.builder()
                .priority(Priority.MEDIUM)
                .preferredDate(LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.THURSDAY)))
                .estimatedValue(BigDecimal.valueOf(250.0))
                .termsAccepted(true)
                .build();
        solicitation = Solicitation.builder()
                .id(1l)
                .client(client)
                .status(StatusSolicitation.DRAFT)
                .currentStep(1)
                .stepOneData(stepOneData)
                .stepTwoData(stepTwoData)
                .stepThreeData(stepThreeData)
                .createdAt(LocalDateTime.now())
                .build();

    }

    @Test
    @DisplayName("Should change status to SUBMITTED when all steps are complete")
    void submit_shouldChangeStatus_whenAllStepsAreComplete() {
        //ARRANGE
        when(repository.findById(solicitation.getId())).thenReturn(Optional.ofNullable(solicitation));

        //ACT
        SolicitationResponseDTO result = service.submit(solicitation.getId(), client);

        //ASSERT
        then(repository).should().save(solicitation);
        Assertions.assertEquals(StatusSolicitation.SUBMITTED, result.status());
        Assertions.assertNotNull(result.updateAt());
        Assertions.assertNotNull(result.submittedAt());
    }

    @Test
    @DisplayName("Should throw exception when step 1 is not complete")
    void submit_shouldThrowException_whenStep1IsNotComplete() {
        //ARRANGE
        solicitation.setStepOneData(null);
        when(repository.findById(solicitation.getId())).thenReturn(Optional.ofNullable(solicitation));

        //ASSERT + ACT
        InvalidOperationException exception = Assertions.assertThrows(InvalidOperationException.class, () -> service.submit(solicitation.getId(), client));
        Assertions.assertEquals("Step 1 is not complete", exception.getMessage());
        then(repository).should(Mockito.never()).save(solicitation);
    }

    @Test
    @DisplayName("")
    void submit_shouldThrowException_whenClientIsNotOwner() {
        //ARRANGE
        when(repository.findById(solicitation.getId())).thenReturn(Optional.ofNullable(solicitation));

        //ASSERT + ACT
        UnauthorizedException exception = Assertions.assertThrows(UnauthorizedException.class, () -> service.submit(solicitation.getId(), client2));
        Assertions.assertEquals("Access denied: you are not the owner of this resource", exception.getMessage());
    }

    @Test
    @DisplayName("")
    void submit_shouldThrowException_whenStatusIsNotDraft() {
        //ARRANGE
        solicitation.setStatus(StatusSolicitation.SUBMITTED);
        when(repository.findById(solicitation.getId())).thenReturn(Optional.ofNullable(solicitation));

        //ASSERT + ACT
        InvalidOperationException exception = Assertions.assertThrows(InvalidOperationException.class, () -> service.submit(solicitation.getId(), client));
        Assertions.assertEquals("Solicitation can only be edited when status is DRAFT", exception.getMessage());
    }

    @Test
    @DisplayName("")
    void submit_shouldThrowException_whenTermsNotAccepted() {
        //ARRANGE
        solicitation.getStepThreeData().setTermsAccepted(false);
        when(repository.findById(solicitation.getId())).thenReturn(Optional.ofNullable(solicitation));

        //ASSERT + ACT
        InvalidOperationException exception = Assertions.assertThrows(InvalidOperationException.class, () -> service.submit(solicitation.getId(), client));
        Assertions.assertEquals("Terms must be accepted to submit the solicitation", exception.getMessage());
    }
}