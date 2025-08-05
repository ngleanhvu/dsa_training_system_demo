package com.ngleanhvu.dsa_training_system.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ngleanhvu.dsa_training_system.constant.KafkaConst;
import com.ngleanhvu.dsa_training_system.dto.request.*;
import com.ngleanhvu.dsa_training_system.dto.response.*;
import com.ngleanhvu.dsa_training_system.entity.*;
import com.ngleanhvu.dsa_training_system.exception.InvalidValueException;
import com.ngleanhvu.dsa_training_system.exception.ResourceNotFoundException;
import com.ngleanhvu.dsa_training_system.repo.*;
import com.ngleanhvu.dsa_training_system.repo.spec.ProblemSpecification;
import com.ngleanhvu.dsa_training_system.service.ProblemService;
import com.ngleanhvu.dsa_training_system.util.AppUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
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
                .isPublic(request.isPublic())
                .status(1)
                .isPublic(request.isPublic())
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

        String hintsJson = objectMapper.writeValueAsString(request.getHints() != null && !request.getHints().isEmpty() ? request.getHints() : Collections.emptyList());

        ProblemDetail problemDetail = ProblemDetail.builder()
                .problem(problem)
                .memoryLimit(request.getMemoryLimit())
                .timeLimit(request.getTimeLimit())
                .description(request.getDescription())
                .constraints(AppUtil.sanitize(request.getConstraints()))
                .hints(hintsJson)
                .status(1)
                .build();


            ProblemDocumentCreateRequest problemDocumentCreateRequest = ProblemDocumentCreateRequest.builder()
                    .problemId(problem.getProblemId())
                    .title(request.getTitle())
                    .url(getSlugPrefix()+problem.getProblemId())
                    .topicTitles(topics.stream().map(Topic::getName).toList())
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
    public ListProblemResponse getProblems(ProblemSearchAdminRequest searchRequest, PagingSearch pagingSearch) {
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
                        .difficulty(DifficultResponse.builder()
                                .difficultId(problem.getDifficulty().getDifficultyId())
                                .difficultName(problem.getDifficulty().getName())
                                .build())
                        .topics(problem.getProblemTopics() != null
                                ? problem.getProblemTopics().stream()
                                .map(p -> TopicResponse.builder()
                                        .topicId(p.getTopic().getTopicId())
                                        .topicName(p.getTopic().getName())
                                        .build())
                                .filter(Objects::nonNull)
                                .toList()
                                : List.of())
                        .isPublic(problem.isPublic())
                        .build())
                .toList();

        log.debug("Fetched {} problem(s) for admin search", responses.size());

        int page = problems.getNumber() + 1;
        int totalPages = problems.getTotalPages();
        int totalElements = problems.getNumberOfElements();

        ListProblemResponse listProblemResponse =ListProblemResponse.builder()
                .problems(responses)
                .page(page)
                .totalElements(totalElements)
                .totalPage(totalPages)
                .build();
        log.debug("ListProblemResponse: {}",listProblemResponse);
        return listProblemResponse;

    }


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateProblem(Integer problemId, ProblemUpdateRequest problemUpdateRequest) throws JsonProcessingException {
        log.info("problem update request: {}", problemUpdateRequest);
        Problem existingProblem = problemRepo.findById(problemId)
                .orElseThrow(() -> new ResourceNotFoundException("Problem", "id", String.valueOf(problemId)));

        ProblemDetail existingProblemDetail = problemDetailRepo.findByProblemId(problemId)
                .orElseThrow(() -> new ResourceNotFoundException("ProblemDetail", "problemId", String.valueOf(problemId)));

        ProblemDocumentUpdateRequest problemDocumentUpdateRequest = new ProblemDocumentUpdateRequest();

        if (problemUpdateRequest.getTitle() != null) {
            existingProblem.setTitle(problemUpdateRequest.getTitle());
            problemDocumentUpdateRequest.setProblemId(existingProblem.getProblemId());
            problemDocumentUpdateRequest.setTitle(problemUpdateRequest.getTitle());
        }

        if (problemUpdateRequest.getDifficultId() != null) {
            Difficulty difficulty = difficultyRepo.findById(problemUpdateRequest.getDifficultId())
                    .orElseThrow(() -> new ResourceNotFoundException("Difficulty", "id", String.valueOf(problemUpdateRequest.getDifficultId())));
            existingProblem.setDifficulty(difficulty);
            problemDocumentUpdateRequest.setDifficultyName(difficulty.getName());
            problemDocumentUpdateRequest.setDifficultyId(difficulty.getDifficultyId());
        }

        if (problemUpdateRequest.getTopicIds() != null) {
            List<Topic> newTopics = topicRepo.findAllById(problemUpdateRequest.getTopicIds());
            List<ProblemTopic> currentProblemTopics = problemTopicRepo.findByProblemId(problemId);

            Set<Integer> currentTopicIds = currentProblemTopics.stream()
                    .map(pt -> pt.getTopic().getTopicId())
                    .collect(Collectors.toSet());

            Set<Integer> newTopicIds = newTopics.stream()
                    .map(Topic::getTopicId)
                    .collect(Collectors.toSet());

            Set<String> newTopicTitles = newTopics.stream()
                            .map(Topic::getName)
                            .collect(Collectors.toSet());

            problemDocumentUpdateRequest.setTopicIds(newTopicIds);
            problemDocumentUpdateRequest.setTopicTitles(newTopicTitles);

            Set<Integer> toRemove = new HashSet<>(currentTopicIds);
            toRemove.removeAll(newTopicIds);

            if (!toRemove.isEmpty()) {
                problemTopicRepo.deleteByProblemIdAndTopicIds(problemId, toRemove);
            }

            Set<Integer> toAdd = new HashSet<>(newTopicIds);
            toAdd.removeAll(currentTopicIds);

            if (!toAdd.isEmpty()) {
                List<Topic> topicsToAdd = newTopics.stream()
                        .filter(t -> toAdd.contains(t.getTopicId()))
                        .toList();

                List<ProblemTopic> newProblemTopics = topicsToAdd.stream()
                        .map(topic -> ProblemTopic.builder()
                                .problem(existingProblem)
                                .topic(topic)
                                .status(1)
                                .build())
                        .toList();

                problemTopicRepo.saveAll(newProblemTopics);
            }
        }

        String constraints = problemUpdateRequest.getConstraints();

        String hintsJson = objectMapper.writeValueAsString(
                problemUpdateRequest.getHints() != null
                        ? problemUpdateRequest.getHints()
                        : existingProblemDetail.getHints()
        );


        existingProblemDetail.setConstraints(constraints);
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


            String problemDocumentUpdateJson = objectMapper.writeValueAsString(problemDocumentUpdateRequest);
            kafkaTemplate.send(KafkaConst.PROBLEM_DOCUMENT_UPDATE_TOPIC, problemDocumentUpdateJson);


        problemRepo.save(existingProblem);
        problemDetailRepo.save(existingProblemDetail);
    }

    @Override
    public void deleteProblem(Integer problemId) throws JsonProcessingException {
       Problem problem =  problemRepo.findById(problemId)
               .orElseThrow(() -> new ResourceNotFoundException("Problem", "id", String.valueOf(problemId)));

           String problemIdJson = objectMapper.writeValueAsString(problemId);
           kafkaTemplate.send(KafkaConst.PROBLEM_DOCUMENT_DELETE_TOPIC, problemIdJson);

       problemRepo.delete(problem);
    }

    @Override
    public ProblemDetailResponse getProblem(Integer problemId) throws JsonProcessingException {
        ProblemDetail problemDetail = problemDetailRepo.findByProblemId(problemId)
                .orElseThrow(() -> new ResourceNotFoundException("Problem detail", "problem id", String.valueOf(problemId)));

        List<TopicResponse> topics = problemDetail.getProblem().getProblemTopics() != null
                ? problemDetail.getProblem().getProblemTopics().stream()
                .map(p -> TopicResponse.builder()
                        .topicId(p.getTopic().getTopicId())
                        .topicName(p.getTopic().getName())
                        .build())
                .filter(Objects::nonNull)
                .toList()
                : List.of();

        List<String> hints = objectMapper.readValue(problemDetail.getHints(), new TypeReference<>() {});

        ProblemDetailResponse response = ProblemDetailResponse.builder()
                .problemId(problemDetail.getProblem().getProblemId())
                .title(problemDetail.getProblem().getTitle())
                .difficult(DifficultResponse.builder()
                        .difficultId(problemDetail.getProblem().getDifficulty().getDifficultyId())
                        .difficultName(problemDetail.getProblem().getDifficulty().getName())
                        .build())
                .topics(topics)
                .description(problemDetail.getDescription())
                .constraints(problemDetail.getConstraints())
                .hints(hints)
                .memoryLimit(problemDetail.getMemoryLimit())
                .timeLimit(problemDetail.getTimeLimit())
                .build();

        log.debug("Fetched problem response: {}", response);

        return response;
    }

    @Transactional
    @Override
    public void togglePublishProblem(Integer problemId) throws JsonProcessingException {
        Problem problem = problemRepo.findById(problemId)
                .orElseThrow(() -> new ResourceNotFoundException("Problem", "id", String.valueOf(problemId)));

        if (!problem.isPublic()) {
            problem.setPublic(true);
            problem.setPublishAt(LocalDateTime.now());

            ToggleRequest toggleRequest = new ToggleRequest();
            toggleRequest.setProblemId(problemId);
            toggleRequest.setPublic(true);

            String toggleJson = objectMapper.writeValueAsString(toggleRequest);
            log.info("problem toggle request: {}", toggleJson);
            kafkaTemplate.send(KafkaConst.PROBLEM_DOCUMENT_TOGGLE_TOPIC, toggleJson);
        } else {
            problem.setPublic(false);

            ToggleRequest toggleRequest = new ToggleRequest();
            toggleRequest.setProblemId(problemId);
            toggleRequest.setPublic(false);

            String toggleJson = objectMapper.writeValueAsString(toggleRequest);

            kafkaTemplate.send(KafkaConst.PROBLEM_DOCUMENT_TOGGLE_TOPIC, toggleJson);
        }

        problemRepo.save(problem);
    }

    private String getSlugPrefix() {
        return String.format("http://%s:%s/api/v1/problems/", serverAddress, serverPort);
    }
}
