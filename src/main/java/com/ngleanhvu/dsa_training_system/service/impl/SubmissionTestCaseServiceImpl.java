package com.ngleanhvu.dsa_training_system.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ngleanhvu.dsa_training_system.constant.KafkaConst;
import com.ngleanhvu.dsa_training_system.dto.request.SubmissionTestCaseCreateRequest;
import com.ngleanhvu.dsa_training_system.dto.request.SubmissionTestCaseRequest;
import com.ngleanhvu.dsa_training_system.entity.Submission;
import com.ngleanhvu.dsa_training_system.entity.SubmissionStatus;
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
import java.util.ArrayList;
import java.util.List;

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

        Submission submission = submissionRepo.findById(submissionTestCaseRequest.getSubmissionId())
                .orElseThrow(() -> new ResourceNotFoundException("Submission", "id", String.valueOf(submissionTestCaseRequest.getSubmissionId())));

        List<Integer> testCaseIds = submissionTestCaseRequest.getSubmissionTestCaseCreateRequests().stream()
                .map(SubmissionTestCaseCreateRequest::getTestCaseId)
                .toList();

        List<TestCase> testCases = testCaseRepo.findAllById(testCaseIds);
        log.info("Test cases: {}", testCases);

        List<SubmissionTestCase> submissionTestCases = new ArrayList<>();

        for (int i = 0; i < testCases.size(); i++) {
            TestCase testCase = testCases.get(i);
            SubmissionStatus status = submissionTestCaseRequest.getSubmissionTestCaseCreateRequests().get(i).getStatus();
            SubmissionTestCase submissionTestCase = SubmissionTestCase.builder()
                    .submissionStatus(status)
                    .submission(submission)
                    .testCase(testCase)
                    .build();
            submissionTestCases.add(submissionTestCase);
        }

        submissionTestCaseRepo.saveAll(submissionTestCases);
    }
}
