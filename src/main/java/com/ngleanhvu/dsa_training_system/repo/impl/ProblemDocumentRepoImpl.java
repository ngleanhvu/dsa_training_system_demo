package com.ngleanhvu.dsa_training_system.repo.impl;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.ngleanhvu.dsa_training_system.document.ProblemDocument;
import com.ngleanhvu.dsa_training_system.dto.request.ProblemSearchRequest;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ProblemDocumentRepoImpl  {

    private final ElasticsearchOperations operations;

    public Page<ProblemDocument> search(ProblemSearchRequest searchRequest, PagingSearch pagingSearch) {
        Query query =  Query.of(q -> q.bool(b -> {
            if (searchRequest.getTitle() != null && !searchRequest.getTitle().isEmpty()) {
                b.must(m -> m.match(t -> t.field("title").query(FieldValue.of(searchRequest.getTitle()))));
            }

            return b;
        }));

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(query)
                .withPageable(pagingSearch.toPageable())
                .build();

        SearchHits<ProblemDocument> hits = operations.search(nativeQuery, ProblemDocument.class);
        return SearchHitSupport.searchPageFor(hits, nativeQuery.getPageable()).map(SearchHit::getContent);
    }
}
