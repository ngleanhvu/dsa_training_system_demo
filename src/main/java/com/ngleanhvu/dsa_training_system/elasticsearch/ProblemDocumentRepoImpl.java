package com.ngleanhvu.dsa_training_system.elasticsearch;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@Component
@RequiredArgsConstructor
public class ProblemDocumentRepoImpl  {

    private final ElasticsearchOperations operations;

    public Page<ProblemDocument> search(ProblemSearchRequest searchRequest, PagingSearch pagingSearch) {

        String filterTypeStr = searchRequest.getFilterType() != null ? searchRequest.getFilterType().toUpperCase() : FilterType.ALL.name();
        boolean isAll = filterTypeStr.equals(FilterType.ALL.name());

        Query query =  Query.of(q -> q.bool(b -> {
            if (searchRequest.getTitle() != null && !searchRequest.getTitle().isEmpty()) {
                b.must(m -> m.match(t -> t.field("title").query(FieldValue.of(searchRequest.getTitle()))));
            }

            applyFilter(searchRequest.getDifficultyIds(), "difficulty.id" ,isAll, b);
            applyFilter(searchRequest.getTopicIds(), "topic" ,isAll, b);

            return b;
        }));

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(query)
                .withPageable(pagingSearch.toPageable())
                .build();

        SearchHits<ProblemDocument> hits = operations.search(nativeQuery, ProblemDocument.class);
        return SearchHitSupport.searchPageFor(hits, nativeQuery.getPageable()).map(SearchHit::getContent);
    }

    private void applyFilter(Map<String, List<Integer>> filterMap, String fieldName, boolean isAll, BoolQuery.Builder b) {
        if (filterMap != null && !filterMap.isEmpty()) {
            String key = filterMap.keySet().iterator().next();
            List<Integer> values = filterMap.get(key);

            if (values != null && !values.isEmpty()) {
                Query query = Query.of(q -> q.terms(t -> t
                        .field(fieldName)
                        .terms(v -> v.value(values.stream().map(FieldValue::of).toList()))
                ));

                if (key.equals(FilterType.IS.name())) {
                    if (isAll) b.must(query);
                    else b.should(query);
                } else if (key.equals(FilterType.IS_NOT.name())) {
                    Query mustNotQuery = Query.of(q -> q.bool(bq -> bq.mustNot(query)));
                    if (isAll) b.must(mustNotQuery);
                    else b.should(mustNotQuery);
                }
            }
        }
    }

}
