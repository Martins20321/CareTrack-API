package com.martinsdev.caretrack.api.repository;

import com.martinsdev.caretrack.api.model.document.SolicitationDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SolicitationSearchRepository extends ElasticsearchRepository<SolicitationDocument, String> {
}
