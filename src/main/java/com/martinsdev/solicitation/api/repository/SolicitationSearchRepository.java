package com.martinsdev.solicitation.api.repository;

import com.martinsdev.solicitation.api.model.document.SolicitationDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SolicitationSearchRepository extends ElasticsearchRepository<SolicitationDocument, String> {
}
