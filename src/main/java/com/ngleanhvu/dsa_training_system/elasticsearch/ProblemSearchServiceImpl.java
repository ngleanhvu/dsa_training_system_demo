package com.ngleanhvu.dsa_training_system.elasticsearch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ngleanhvu.dsa_training_system.constant.KafkaConst;
import com.ngleanhvu.dsa_training_system.dto.request.ProblemDocumentCreateRequest;
import com.ngleanhvu.dsa_training_system.dto.request.ProblemDocumentUpdateAcceptRateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProblemSearchServiceImpl implements ProblemSearchService {

    private final ProblemDocumentRepoImpl problemDocumentRepoImpl;
    private final ObjectMapper objectMapper;
    private final ProblemDocumentRepo problemDocumentRepo;

    @Override
    public Page<ProblemDocument> search(ProblemSearchRequest request, PagingSearch pagingSearch) {
        return problemDocumentRepoImpl.search(request, pagingSearch);
    }

    @TransactionalEventListener
    @KafkaListener(topics = KafkaConst.PROBLEM_DOCUMENT_CREATE_TOPIC, groupId = KafkaConst.GROUP_ID)
    public void createProblemDocument(String json) throws JsonProcessingException {
        ProblemDocumentCreateRequest problemDocumentCreateRequest = objectMapper.readValue(json, ProblemDocumentCreateRequest.class);
        log.info("problem create document request: {}", problemDocumentCreateRequest);
        ProblemDocument problemDocument = ProblemDocument.builder()
                .id(problemDocumentCreateRequest.getProblemId())
                .slug(problemDocumentCreateRequest.getSlug())
                .title(problemDocumentCreateRequest.getTitle())
                .createdAt(problemDocumentCreateRequest.getCreatedAt())
                .acceptanceRate(problemDocumentCreateRequest.getAcceptanceRate())
                .topic(problemDocumentCreateRequest.getTopicIds())
                .difficultyId(problemDocumentCreateRequest.getDifficultyId())
                .difficultyName(problemDocumentCreateRequest.getDifficultyName())
                .build();

        problemDocumentRepo.save(problemDocument);
    }

    @KafkaListener(topics = KafkaConst.PROBLEM_DOCUMENT_UPDATE_ACCEPT_RATE_TOPIC, groupId = KafkaConst.GROUP_ID)
    public void updateAcceptRate(String json) throws JsonProcessingException {
        ProblemDocumentUpdateAcceptRateRequest problemDocumentUpdateAcceptRateRequest = objectMapper.readValue(json, ProblemDocumentUpdateAcceptRateRequest.class);
        log.info("problem update accept rate request: {}", problemDocumentUpdateAcceptRateRequest);
        problemDocumentRepoImpl.updateAcceptRateByProblemId(problemDocumentUpdateAcceptRateRequest.getProblemId(),
                problemDocumentUpdateAcceptRateRequest.getAcceptRate());
    }

}
