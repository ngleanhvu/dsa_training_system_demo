package com.ngleanhvu.dsa_training_system.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ngleanhvu.dsa_training_system.constant.KafkaConst;
import com.ngleanhvu.dsa_training_system.dto.request.SubmissionTestCaseCreateRequest;
import com.ngleanhvu.dsa_training_system.dto.request.SubmissionTestCaseRequest;
import com.ngleanhvu.dsa_training_system.entity.Submission;
import com.ngleanhvu.dsa_training_system.entity.SubmissionTestCase;
import com.ngleanhvu.dsa_training_system.entity.TestCase;
import com.ngleanhvu.dsa_training_system.exception.ResourceNotFoundException;
import com.ngleanhvu.dsa_training_system.repo.SubmissionRepo;
import com.ngleanhvu.dsa_training_system.repo.SubmissionTestCaseRepo;
import com.ngleanhvu.dsa_training_system.repo.TestCaseRepo;
import com.ngleanhvu.dsa_training_system.service.SubmissionTestCaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor

public class SubmissionTestCaseServiceImpl implements SubmissionTestCaseService {

    private final SubmissionTestCaseRepo submissionTestCaseRepo;
    private final TestCaseRepo testCaseRepo;
    private final ObjectMapper objectMapper;
    private final SubmissionRepo submissionRepo;

    @Transactional
    @KafkaListener(topics = KafkaConst.SUBMISSION_TEST_CASE_CREATE_TOPIC, groupId = KafkaConst.GROUP_ID)
    @Override
    public void createSubmissionTestCase(String json) throws JsonProcessingException {

        SubmissionTestCaseRequest submissionTestCaseRequest = objectMapper.readValue(json, SubmissionTestCaseRequest.class);
        log.info("submissionTestCaseRequest received: {}", submissionTestCaseRequest);

        Submission submission = submissionRepo.findById(submissionTestCaseRequest.getSubmissionId())
                .orElseThrow(() -> new ResourceNotFoundException("Submission", "id", String.valueOf(submissionTestCaseRequest.getSubmissionId())));

        // Lấy toàn bộ testCase theo id
        List<Integer> testCaseIds = submissionTestCaseRequest.getSubmissionTestCaseCreateRequests().stream()
                .map(SubmissionTestCaseCreateRequest::getTestCaseId)
                .toList();

        List<TestCase> testCases = testCaseRepo.findAllById(testCaseIds);
        log.info("Test cases: {}", testCases);

        // Map testCaseId -> TestCase
        Map<Integer, TestCase> testCaseMap = testCases.stream()
                .collect(Collectors.toMap(TestCase::getTestCaseId, Function.identity()));

        List<SubmissionTestCase> submissionTestCases = submissionTestCaseRequest.getSubmissionTestCaseCreateRequests()
                .stream()
                .map(req -> SubmissionTestCase.builder()
                        .submissionStatus(req.getStatus())
                        .submission(submission)
                        .memoryKb(req.getMemory())
                        .runtimeMs(req.getRuntime())
                        .testCase(testCaseMap.get(req.getTestCaseId())) // match đúng TestCase
                        .status(1)
                        .build()
                )
                .toList();

        submissionTestCaseRepo.saveAll(submissionTestCases);
    }

}
