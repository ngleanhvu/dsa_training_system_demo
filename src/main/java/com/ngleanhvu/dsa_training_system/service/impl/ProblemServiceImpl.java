package com.ngleanhvu.dsa_training_system.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ngleanhvu.dsa_training_system.constant.KafkaConst;
import com.ngleanhvu.dsa_training_system.dto.request.ProblemCreateRequest;
import com.ngleanhvu.dsa_training_system.dto.request.ProblemDocumentCreateRequest;
import com.ngleanhvu.dsa_training_system.dto.request.ProblemSearchAdminRequest;
import com.ngleanhvu.dsa_training_system.dto.request.ProblemUpdateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import com.ngleanhvu.dsa_training_system.dto.response.ProblemResponse;
import com.ngleanhvu.dsa_training_system.entity.*;
import com.ngleanhvu.dsa_training_system.exception.InvalidValueException;
import com.ngleanhvu.dsa_training_system.exception.ResourceNotFoundException;
import com.ngleanhvu.dsa_training_system.repo.*;
import com.ngleanhvu.dsa_training_system.repo.spec.ProblemSpecification;
import com.ngleanhvu.dsa_training_system.service.ProblemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProblemServiceImpl implements ProblemService {

    private final ProblemRepo problemRepo;
    private final ProblemDetailRepo problemDetailRepo;
    private final DifficultyRepo difficultyRepo;
    private final TopicRepo topicRepo;
    private final ProblemTopicRepo problemTopicRepo;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${server.port}")
    private String serverPort;

    @Value("${server.address}")
    private String serverAddress;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void createProblem(ProblemCreateRequest request) throws JsonProcessingException {

        Difficulty difficulty = difficultyRepo.findById(request.getDifficultId())
                .orElseThrow(() ->  new ResourceNotFoundException("Difficulty", "id", String.valueOf(request.getDifficultId())));

        Problem problem = Problem.builder()
                .title(request.getTitle())
                .difficulty(difficulty)
                .status(1)
                .build();

        problemRepo.save(problem);

        List<Topic> topics = topicRepo.findAllById(request.getTopicIds());

        if (topics.size() != request.getTopicIds().size()) {
            throw new InvalidValueException("Need least one topic");
        }

        List<ProblemTopic> problemTopics = new ArrayList<>();
        for (Topic topic : topics) {
            ProblemTopic problemTopic = new ProblemTopic();
            problemTopic.setProblem(problem);
            problemTopic.setTopic(topic);
            problemTopics.add(problemTopic);
        }

        problemTopicRepo.saveAll(problemTopics);

        String constraintsJson = objectMapper.writeValueAsString(!request.getConstraints().isEmpty() ? request.getConstraints() : Collections.emptyList());
        String hintsJson = objectMapper.writeValueAsString(request.getHints() != null && !request.getHints().isEmpty() ? request.getHints() : Collections.emptyList());

        ProblemDetail problemDetail = ProblemDetail.builder()
                .problem(problem)
                .memoryLimit(request.getMemoryLimit())
                .timeLimit(request.getTimeLimit())
                .description(request.getDescription())
                .constraints(constraintsJson)
                .hints(hintsJson)
                .status(1)
                .build();

        ProblemDocumentCreateRequest problemDocumentCreateRequest = ProblemDocumentCreateRequest.builder()
                        .problemId(problem.getProblemId())
                        .title(request.getTitle())
                        .slug(getSlugPrefix()+problem.getProblemId())
                        .createdAt(problem.getCreatedAt().toLocalDate())
                        .acceptanceRate(0)
                        .difficultyId(difficulty.getDifficultyId())
                        .difficultyName(difficulty.getName())
                        .topicIds(topics.stream().map(Topic::getTopicId).collect(Collectors.toList()))
                        .build();

        log.info("problem create document request: {}", problemDocumentCreateRequest);

        String problemDocumentJson = objectMapper.writeValueAsString(problemDocumentCreateRequest);
        log.info("problem create document request: {}", problemDocumentJson);
        kafkaTemplate.send(KafkaConst.PROBLEM_DOCUMENT_CREATE_TOPIC, problemDocumentJson);

        problemDetailRepo.save(problemDetail);
    }

    @Override
    public List<ProblemResponse> getProblems(ProblemSearchAdminRequest searchRequest, PagingSearch pagingSearch) {
        Specification<Problem> spec =
                ProblemSpecification.hasTitle(searchRequest.getTitle())
                        .and(ProblemSpecification.hasDifficulty(searchRequest.getDifficultyId()))
                        .and(ProblemSpecification.hasTopic(searchRequest.getTopicIds()))
                        .and(ProblemSpecification.hasQuestionId(searchRequest.getQuestionIdRange()))
                        .and(ProblemSpecification.hasPublishDate(searchRequest.getPublishDate()));

        Page<Problem> problems = problemRepo.findAll(spec, pagingSearch.toPageable());

        List<ProblemResponse> responses = problems.getContent().stream()
                .map(problem -> ProblemResponse.builder()
                        .problemId(problem.getProblemId())
                        .title(problem.getTitle())
                        .difficulty(problem.getDifficulty())
                        .topics(problem.getProblemTopics() != null
                                ? problem.getProblemTopics().stream()
                                .map(ProblemTopic::getTopic)
                                .filter(Objects::nonNull)
                                .toList()
                                : List.of())
                        .build())
                .toList();

        log.debug("Fetched {} problem(s) for admin search", responses.size());

        return responses;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateProblem(Integer problemId, ProblemUpdateRequest problemUpdateRequest) throws JsonProcessingException {
        Problem existingProblem = problemRepo.findById(problemId)
                .orElseThrow(() -> new ResourceNotFoundException("Problem", "id", String.valueOf(problemId)));

        ProblemDetail existingProblemDetail = problemDetailRepo.findByProblemId(problemId)
                .orElseThrow(() -> new ResourceNotFoundException("ProblemDetail", "problemId", String.valueOf(problemId)));

        if (problemUpdateRequest.getTitle() != null) {
            existingProblem.setTitle(problemUpdateRequest.getTitle());
        }

        if (problemUpdateRequest.getDifficultId() != null) {
            Difficulty difficulty = difficultyRepo.findById(problemUpdateRequest.getDifficultId())
                    .orElseThrow(() -> new ResourceNotFoundException("Difficulty", "id", String.valueOf(problemUpdateRequest.getDifficultId())));
            existingProblem.setDifficulty(difficulty);
        }

        if (problemUpdateRequest.getTopicIds() != null) {
            List<Topic> topics = topicRepo.findAllById(problemUpdateRequest.getTopicIds());

            List<ProblemTopic> problemTopics = problemTopicRepo.findByProblemId(problemId);
            problemTopicRepo.deleteAll(problemTopics);

            if (!topics.isEmpty()) {
                List<ProblemTopic> newProblemTopics = topics.stream()
                        .map(topic -> ProblemTopic.builder()
                                .status(1)
                                .problem(existingProblem)
                                .topic(topic)
                                .build())
                        .toList();
                problemTopicRepo.saveAll(newProblemTopics);
            }
        }

        String constraintsJson = objectMapper.writeValueAsString(
                (problemUpdateRequest.getConstraints() != null && !problemUpdateRequest.getConstraints().isEmpty())
                        ? problemUpdateRequest.getConstraints()
                        : existingProblemDetail.getConstraints()
        );

        String hintsJson = objectMapper.writeValueAsString(
                problemUpdateRequest.getHints() != null
                        ? problemUpdateRequest.getHints()
                        : existingProblemDetail.getHints()
        );

        existingProblemDetail.setConstraints(constraintsJson);
        existingProblemDetail.setHints(hintsJson);

        if (problemUpdateRequest.getDescription() != null) {
            existingProblemDetail.setDescription(problemUpdateRequest.getDescription());
        }

        if (problemUpdateRequest.getMemoryLimit() != null) {
            existingProblemDetail.setMemoryLimit(problemUpdateRequest.getMemoryLimit());
        }

        if (problemUpdateRequest.getTimeLimit() != null) {
            existingProblemDetail.setTimeLimit(problemUpdateRequest.getTimeLimit());
        }

        problemRepo.save(existingProblem);
        problemDetailRepo.save(existingProblemDetail);
    }

    @Override
    public void deleteProblem(Integer problemId) {
        problemRepo.deleteById(problemId);
    }

    @Override
    public ProblemResponse getProblem(Integer problemId) {
        Problem problem = problemRepo.findById(problemId)
                .orElseThrow(() -> new ResourceNotFoundException("Problem", "id", String.valueOf(problemId)));

        List<Topic> topics = problem.getProblemTopics() != null
                ? problem.getProblemTopics().stream()
                .map(ProblemTopic::getTopic)
                .filter(Objects::nonNull)
                .toList()
                : List.of();

        ProblemResponse response = ProblemResponse.builder()
                .problemId(problem.getProblemId())
                .title(problem.getTitle())
                .difficulty(problem.getDifficulty())
                .topics(topics)
                .build();

        log.debug("Fetched problem response: {}", response);

        return response;
    }


    private String getSlugPrefix() {
        return String.format("http://%s:%s/api/v1/problems/", serverAddress, serverPort);
    }
}
