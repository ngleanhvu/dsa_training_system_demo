package com.ngleanhvu.dsa_training_system.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ngleanhvu.dsa_training_system.constant.KafkaConst;
import com.ngleanhvu.dsa_training_system.dto.request.ContestSubmissionCreateRequest;
import com.ngleanhvu.dsa_training_system.entity.Contest;
import com.ngleanhvu.dsa_training_system.entity.ContestSubmission;
import com.ngleanhvu.dsa_training_system.entity.Submission;
import com.ngleanhvu.dsa_training_system.exception.ResourceNotFoundException;
import com.ngleanhvu.dsa_training_system.repo.ContestRepo;
import com.ngleanhvu.dsa_training_system.repo.ContestSubmissionRepo;
import com.ngleanhvu.dsa_training_system.repo.SubmissionRepo;
import com.ngleanhvu.dsa_training_system.service.ContestSubmissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContestSubmissionServiceImpl implements ContestSubmissionService {

    private final ContestSubmissionRepo contestSubmissionRepo;
    private final ContestRepo contestRepo;
    private final SubmissionRepo submissionRepo;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = KafkaConst.CONTEST_SUBMISSION_CREATE_TOPIC, groupId = KafkaConst.GROUP_ID)
    @Override
    @Transactional
    public void createContestSubmission(String json) throws JsonProcessingException {
        ContestSubmissionCreateRequest contestSubmissionCreateRequest = objectMapper.readValue(json, ContestSubmissionCreateRequest.class);

        Contest contest = contestRepo.findById(contestSubmissionCreateRequest.getContestId())
                .orElseThrow(() -> new ResourceNotFoundException("Contest","id",String.valueOf(contestSubmissionCreateRequest.getContestId())));

        Submission submission = submissionRepo.findById(contestSubmissionCreateRequest.getSubmissionId())
                .orElseThrow(() -> new ResourceNotFoundException("Submission","id",String.valueOf(contestSubmissionCreateRequest.getSubmissionId())));

        ContestSubmission contestSubmission = ContestSubmission.builder()
                .contest(contest)
                .submission(submission)
                .score(contestSubmissionCreateRequest.getScore())
                .status(1)
                .build();

        contestSubmissionRepo.save(contestSubmission);
    }
}
