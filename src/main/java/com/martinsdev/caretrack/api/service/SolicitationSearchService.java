package com.martinsdev.caretrack.api.service;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import com.martinsdev.caretrack.api.dto.SolicitationSearchRequestDTO;
import com.martinsdev.caretrack.api.infra.exception.ResourceNotFoundException;
import com.martinsdev.caretrack.api.model.AnalystCoverage;
import com.martinsdev.caretrack.api.model.User;
import com.martinsdev.caretrack.api.model.document.SolicitationDocument;
import com.martinsdev.caretrack.api.model.enums.RoleUser;
import com.martinsdev.caretrack.api.repository.AnalystCoverageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SolicitationSearchService {

    private final ElasticsearchOperations operations;
    private final AnalystCoverageRepository coverageRepository;

    public Page<SolicitationDocument> search(SolicitationSearchRequestDTO searchRequestDTO, User user) {

        BoolQuery.Builder boolQuery = new BoolQuery.Builder();

        //Busca textual 'q' para title e description
        if (searchRequestDTO.q() != null && !searchRequestDTO.q().isBlank()) {
            boolQuery.must(MultiMatchQuery.of(m -> m.query(searchRequestDTO.q()).fields("title", "description"))._toQuery());
        }

        //Filtros exatos
        if (searchRequestDTO.status() != null) {
            boolQuery.filter(TermQuery.of(
                    t -> t.field("status").value(searchRequestDTO.status()))._toQuery());
        }

        if (searchRequestDTO.serviceType() != null) {
            boolQuery.filter(TermQuery.of(
                    t -> t.field("serviceType").value(searchRequestDTO.serviceType()))._toQuery());
        }

        if (searchRequestDTO.priority() != null) {
            boolQuery.filter(TermQuery.of(
                    t -> t.field("priority").value(searchRequestDTO.priority()))._toQuery());
        }

        //Filtros de state - forçado para o Analyst
        if (user.getRole() == RoleUser.ANALYST) {
            AnalystCoverage coverage = coverageRepository.findByUser(user)
                    .orElseThrow(() -> new ResourceNotFoundException("Coverage not found"));
            boolQuery.filter(TermsQuery.of(
                    t -> t.field("state")
                            .terms(tr -> tr.value(coverage.getStates().stream().map(FieldValue::of).toList())))._toQuery());
        } else if (searchRequestDTO.state() != null) {
            boolQuery.filter(TermQuery.of(t -> t.field("state").value(searchRequestDTO.state()))._toQuery());
        }

        //Filtro de data
        if (searchRequestDTO.dateFrom() != null || searchRequestDTO.dateTo() != null) {
            boolQuery.filter(RangeQuery.of(r -> r
                    .date(d -> d.field("createdAt")
                            .gte(searchRequestDTO.dateFrom() != null ? searchRequestDTO.dateFrom().toString() : null)
                            .lte(searchRequestDTO.dateTo() != null ? searchRequestDTO.dateTo().toString() : null)))._toQuery());
        }

        NativeQuery query = NativeQuery.builder()
                .withQuery(boolQuery.build()._toQuery())
                .withPageable(PageRequest.of(searchRequestDTO.page(), searchRequestDTO.size()))
                .build();

        SearchHits<SolicitationDocument> hits = operations.search(query, SolicitationDocument.class);

        return new PageImpl<>(
                hits.stream().map(SearchHit::getContent).toList(),
                PageRequest.of(searchRequestDTO.page(), searchRequestDTO.size()),
                hits.getTotalHits());
    }
}
