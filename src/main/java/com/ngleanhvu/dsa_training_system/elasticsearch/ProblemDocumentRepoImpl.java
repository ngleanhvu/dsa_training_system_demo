package com.ngleanhvu.dsa_training_system.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import com.ngleanhvu.dsa_training_system.dto.request.RangeRequest;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProblemDocumentRepoImpl  {

    private final ElasticsearchOperations operations;
    private final ElasticsearchClient elasticsearchClient;


    public Page<ProblemDocument> search(ProblemSearchRequest searchRequest, PagingSearch pagingSearch) {
        String filterTypeStr = (searchRequest.getFilterType() == null || searchRequest.getFilterType().isBlank())
                ? FilterType.ALL.name()
                : searchRequest.getFilterType().toUpperCase();
        log.info("filterTypeStr: {}", filterTypeStr);

        boolean isAll = filterTypeStr.equals(FilterType.ALL.name());
        log.info("isAll: {}", isAll);

        Query query;
        if (isEmpty(searchRequest)) {
            query = Query.of(q -> q.matchAll(ma -> ma));
            log.info("query: {}", query);
        } else {
            query = Query.of(q -> q.bool(b -> {
                if (searchRequest.getTitle() != null && !searchRequest.getTitle().isEmpty()) {
                    b.must(m -> m.match(t -> t.field("title").query(FieldValue.of(searchRequest.getTitle()))));
                }

                applyFilter(searchRequest.getDifficultyIds(), "difficultyId", isAll, b);
                applyFilter(searchRequest.getTopicIds(), "topic", isAll, b);
                applyDateRange(searchRequest.getPublishedDateRange(), "createdAt", isAll, b);
                applyNumberRange(searchRequest.getAcceptanceRateRange(), "acceptanceRate", isAll, b);
                applyNumberRange(searchRequest.getQuestionIdRange(), "id", isAll, b);
                return b;
            }));
        }

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(query)
                .withPageable(pagingSearch.toPageable())
                .build();

        log.info("nativeQuery: {}", nativeQuery.getQuery());

        log.info("pageable: {}", pagingSearch.toPageable());

        try {
            SearchHits<ProblemDocument> hits = operations.search(nativeQuery, ProblemDocument.class);
            log.info("hits: {}", hits);
            return SearchHitSupport.searchPageFor(hits, nativeQuery.getPageable()).map(SearchHit::getContent);
        } catch (Exception e) {
            log.error("Elasticsearch search failed", e);
            throw new RuntimeException("ES query failed", e);
        }
    }

    public void updateAcceptRateByProblemId(Integer problemId, double acceptRate) {
        log.info("updateAcceptRateByProblemId: {}", problemId);
        log.info("acceptRate: {}", acceptRate);
        try {
            elasticsearchClient.update(u -> u
                            .index("problem_index")
                            .id(problemId.toString())
                            .doc(Map.of("acceptanceRate", acceptRate))
                    , Map.class);
        } catch (IOException e) {
            log.error("Elasticsearch update failed", e);
        }
    }

    private <T> void applyDateRange(RangeRequest<T> range, String fieldName, boolean isAll, BoolQuery.Builder b) {
        if (range == null || (range.getFrom() == null && range.getTo() == null)) {
            return;
        }

        Query query = Query.of(q -> q.range(r -> r.date(DateRangeQuery.of(f -> f.field(fieldName)
                .gte((String) range.getFrom())
                .lte((String) range.getTo()))
        )));

        if (isAll) {
            b.must(query);
        } else {
            b.should(query);
        }
    }

    private <T> void applyNumberRange(RangeRequest<T> range, String fieldName, boolean isAll, BoolQuery.Builder b) {
        if (range == null || (range.getFrom() == null && range.getTo() == null)) {
            return;
        }

        log.info("range: {}", range);

        Query query = Query.of(q -> q.range(r -> r.number(NumberRangeQuery.of(f -> f.field(fieldName)
                .gte((Double) range.getFrom())
                .lte((Double) range.getTo()))
        )));

        if (isAll) {
            b.must(query);
        } else {
            b.should(query);
        }
    }

    private void applyFilter(Map<String, List<Integer>> filterMap, String fieldName, boolean isAll, BoolQuery.Builder b) {
        if (filterMap != null && !filterMap.isEmpty()) {
            String key = filterMap.keySet().iterator().next();
            log.info("key: {}", key);
            List<Integer> values = filterMap.get(key);
            log.info("values: {}", values);
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

    private boolean isEmpty(ProblemSearchRequest request) {
        return (request.getTitle() == null || request.getTitle().isBlank())
                && (request.getDifficultyIds() == null || request.getDifficultyIds().isEmpty())
                && (request.getTopicIds() == null || request.getTopicIds().isEmpty())
                && (request.getStatus() == null || request.getStatus().isEmpty())
                && (request.getAcceptanceRateRange() == null)
                && (request.getPublishedDateRange() == null)
                && (request.getQuestionIdRange() == null);
    }
}