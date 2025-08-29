package com.ngleanhvu.dsa_training_system.elasticsearch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ngleanhvu.dsa_training_system.constant.KafkaConst;
import com.ngleanhvu.dsa_training_system.dto.request.ProblemDocumentCreateRequest;
import com.ngleanhvu.dsa_training_system.dto.request.ProblemDocumentUpdateAcceptRateRequest;
import com.ngleanhvu.dsa_training_system.dto.request.ProblemDocumentUpdateRequest;
import com.ngleanhvu.dsa_training_system.dto.request.ToggleRequest;
import com.ngleanhvu.dsa_training_system.dto.response.ListProblemDocumentResponse;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import com.ngleanhvu.dsa_training_system.dto.response.ProblemDocumentResponse;
import com.ngleanhvu.dsa_training_system.repo.SubmissionRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProblemSearchServiceImpl implements ProblemSearchService {

    private final ProblemDocumentRepoImpl problemDocumentRepoImpl;
    private final ObjectMapper objectMapper;
    private final ProblemDocumentRepo problemDocumentRepo;
    private final SubmissionRepo submissionRepo;

    @Override
    public ListProblemDocumentResponse search(ProblemSearchRequest request, PagingSearch pagingSearch) {
        var problemDocumentPage =  problemDocumentRepoImpl.search(request, pagingSearch);
        var problemDocuments = problemDocumentPage.getContent();
        var problemIds = problemDocuments.stream()
                .map(ProblemDocument::getId)
                .toList();

        var problemSolved = submissionRepo.getSubmissionByUserIdAndProblemStatus(request.getUserId(),
                    problemIds);

        log.info("problemSolved: {}", problemSolved);
        Set<Integer> problemSolvedIdsSet = problemSolved.stream()
                .map(s -> s.getProblem().getProblemId())
                .collect(Collectors.toSet());
        log.info("problemSolvedIdsSet: {}", problemSolvedIdsSet);
        List<ProblemDocumentResponse> problemDocumentResponses = problemDocuments.stream()
                .map(p -> ProblemDocumentResponse.builder()
                        .id(p.getId())
                        .title(p.getTitle())
                        .acceptanceRate(p.getAcceptanceRate()+"%")
                        .topicIds(p.getTopic().stream().toList())
                        .createdAt(p.getCreatedAt())
                        .difficultyId(p.getDifficultyId())
                        .difficultyName(p.getDifficultyName())
                        .isPublic(p.isPublic())
                        .isAccepted(problemSolvedIdsSet.contains(p.getId()))
                        .topicNames(p.getTopicTitle())
                        .url(p.getUrl())
                        .build()
                )
                .toList();
        return ListProblemDocumentResponse.builder()
                .problemDocumentResponses(problemDocumentResponses)
                .page(problemDocumentPage.getNumber()+1)
                .totalPages(problemDocumentPage.getTotalPages())
                .totalElements((int) problemDocumentPage.getTotalElements())
                .build();
    }

    @TransactionalEventListener
    @KafkaListener(topics = KafkaConst.PROBLEM_DOCUMENT_CREATE_TOPIC, groupId = KafkaConst.GROUP_ID)
    public void createProblemDocument(String json) throws JsonProcessingException {
        ProblemDocumentCreateRequest problemDocumentCreateRequest = objectMapper.readValue(json, ProblemDocumentCreateRequest.class);
        log.info("problem create document request: {}", problemDocumentCreateRequest);
        ProblemDocument problemDocument = ProblemDocument.builder()
                .id(problemDocumentCreateRequest.getProblemId())
                .url(problemDocumentCreateRequest.getUrl())
                .title(problemDocumentCreateRequest.getTitle())
                .createdAt(problemDocumentCreateRequest.getCreatedAt())
                .acceptanceRate(problemDocumentCreateRequest.getAcceptanceRate())
                .topicTitle(problemDocumentCreateRequest.getTopicTitles())
                .topic(problemDocumentCreateRequest.getTopicIds())
                .difficultyId(problemDocumentCreateRequest.getDifficultyId())
                .difficultyName(problemDocumentCreateRequest.getDifficultyName())
                .build();

        problemDocumentRepo.save(problemDocument);
    }

    @TransactionalEventListener
    @KafkaListener(topics = KafkaConst.PROBLEM_DOCUMENT_UPDATE_ACCEPT_RATE_TOPIC, groupId = KafkaConst.GROUP_ID)
    public void updateAcceptRate(String json) throws JsonProcessingException {
        ProblemDocumentUpdateAcceptRateRequest problemDocumentUpdateAcceptRateRequest = objectMapper.readValue(json, ProblemDocumentUpdateAcceptRateRequest.class);
        problemDocumentRepoImpl.updateAcceptRateByProblemId(problemDocumentUpdateAcceptRateRequest.getProblemId(),
                problemDocumentUpdateAcceptRateRequest.getAcceptRate());
    }

    @TransactionalEventListener
    @KafkaListener(topics = KafkaConst.PROBLEM_DOCUMENT_DELETE_TOPIC, groupId = KafkaConst.GROUP_ID)
    public void deleteProblemDocument(String json) throws JsonProcessingException {
        Integer problemId = objectMapper.readValue(json, Integer.class);
        problemDocumentRepoImpl.deleteByProblemId(problemId);
    }

    @TransactionalEventListener
    @KafkaListener(topics = KafkaConst.PROBLEM_DOCUMENT_TOGGLE_TOPIC, groupId = KafkaConst.GROUP_ID)
    public void toggleProblemDocument(String json) throws JsonProcessingException {
        ToggleRequest toggleRequest = objectMapper.readValue(json, ToggleRequest.class);
        problemDocumentRepoImpl.toggleProblemById(toggleRequest);
    }

    @TransactionalEventListener
    @KafkaListener(topics = KafkaConst.PROBLEM_DOCUMENT_UPDATE_TOPIC, groupId = KafkaConst.GROUP_ID)
    public void updateTopic(String json) throws JsonProcessingException {
        ProblemDocumentUpdateRequest problemDocumentUpdateRequest = objectMapper.readValue(json, ProblemDocumentUpdateRequest.class);
        problemDocumentRepoImpl.updateProblemsByProblemId(problemDocumentUpdateRequest);
    }
}
