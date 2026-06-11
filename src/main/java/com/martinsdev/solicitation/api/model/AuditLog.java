package com.martinsdev.solicitation.api.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_audit_logs")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String action;
    private String userEmail;
    private String userRole;
    private Long durationMs;
    private boolean success;
    private String errorMessage;
    private LocalDateTime createdAt;
}
