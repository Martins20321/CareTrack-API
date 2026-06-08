package com.martinsdev.solicitation.api.model.document;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDateTime;

@Document(indexName = "solicitations")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class SolicitationDocument {

    private String id;
    private String clientId;
    private String status;
    private String serviceType;
    private String title;
    private String description;
    private String state;
    private String city;
    private String priority;
    private LocalDateTime createdAt;
    private LocalDateTime submittedAt;
}
