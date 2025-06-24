package com.ngleanhvu.dsa_training_system.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ngleanhvu.dsa_training_system.dto.request.ProblemCreateRequest;
import com.ngleanhvu.dsa_training_system.entity.*;
import com.ngleanhvu.dsa_training_system.exception.InvalidValueException;
import com.ngleanhvu.dsa_training_system.exception.ResourceNotFoundException;
import com.ngleanhvu.dsa_training_system.repo.*;
import com.ngleanhvu.dsa_training_system.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProblemServiceImpl implements ProblemService {

    private final ProblemRepo problemRepo;
    private final ProblemDetailRepo problemDetailRepo;
    private final DifficultyRepo difficultyRepo;
    private final TopicRepo topicRepo;
    private final ProblemTopicRepo problemTopicRepo;
    private final ObjectMapper objectMapper;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void createProblem(ProblemCreateRequest request) throws JsonProcessingException {

        Difficulty difficulty = difficultyRepo.findById(request.getDifficultId())
                .orElseThrow(() ->  new ResourceNotFoundException("Difficulty", "id", String.valueOf(request.getDifficultId())));

        Problem problem = Problem.builder()
                .title(request.getTitle())
                .difficulty(difficulty)
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
        String hintsJson = objectMapper.writeValueAsString(!request.getHints().isEmpty() ? request.getHints() : Collections.emptyList());

        ProblemDetail problemDetail = ProblemDetail.builder()
                .problem(problem)
                .memoryLimit(request.getMemoryLimit())
                .timeLimit(request.getTimeLimit())
                .description(request.getDescription())
                .constraints(constraintsJson)
                .hints(hintsJson)
                .build();

        problemDetailRepo.save(problemDetail);
    }
}
