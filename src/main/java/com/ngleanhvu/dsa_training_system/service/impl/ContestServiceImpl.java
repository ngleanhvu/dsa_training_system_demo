package com.ngleanhvu.dsa_training_system.service.impl;

import com.ngleanhvu.dsa_training_system.dto.request.ContestCreateRequest;
import com.ngleanhvu.dsa_training_system.dto.request.ContestFilterRequest;
import com.ngleanhvu.dsa_training_system.dto.request.ContestUpdateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.ContestDetailResponse;
import com.ngleanhvu.dsa_training_system.dto.response.ContestResponse;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import com.ngleanhvu.dsa_training_system.entity.Contest;
import com.ngleanhvu.dsa_training_system.entity.ContestStatus;
import com.ngleanhvu.dsa_training_system.exception.ResourceNotFoundException;
import com.ngleanhvu.dsa_training_system.mappter.ContestMapper;
import com.ngleanhvu.dsa_training_system.repo.ContestRepo;
import com.ngleanhvu.dsa_training_system.repo.spec.ContestSpecification;
import com.ngleanhvu.dsa_training_system.service.ContestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContestServiceImpl implements ContestService {

    private final ContestRepo contestRepo;

    @Transactional
    @Override
    public void createContest(ContestCreateRequest request) {
        Contest contestEntity = Contest.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .startTime(LocalDateTime.parse(request.getStartTime()))
                .endTime(LocalDateTime.parse(request.getEndTime()))
                .durationMinutes(request.getDurationMinutes())
                .build();
        contestRepo.save(contestEntity);
    }

    @Transactional
    @Override
    public void updateContest(Integer contestId, ContestUpdateRequest contest) {
        Contest existingContest = contestRepo.findById(contestId)
                .orElseThrow(() -> new ResourceNotFoundException("Contest", "id", String.valueOf(contestId)));
        existingContest.setTitle(contest.getTitle());
        existingContest.setDescription(contest.getDescription() != null ? contest.getDescription() : existingContest.getTitle());
        existingContest.setContestStatus(ContestStatus.valueOf(contest.getStatus() != null ? contest.getStatus() : existingContest.getContestStatus().name()));
        existingContest.setStartTime(LocalDateTime.parse(contest.getStartTime()));
        existingContest.setDurationMinutes(contest.getDurationMinutes());
        existingContest.setEndTime(LocalDateTime.parse(contest.getEndTime()));
        contestRepo.save(existingContest);
    }

    @Override
    public void deleteContest(Integer contestId) {
        contestRepo.deleteById(contestId);
    }

    @Override
    public List<ContestResponse> getContests(ContestFilterRequest contestFilterRequest, PagingSearch pagingSearch) {
        Specification<Contest> spec = ContestSpecification.hasTitle(contestFilterRequest.getKeyword())
                .and(ContestSpecification.hasTimestamp(contestFilterRequest.getTimestamp()))
                .and(ContestSpecification.hasStatus(contestFilterRequest.getStatus()))
                .and(ContestSpecification.hashContestId(contestFilterRequest.getContestId()));

        Page<Contest> contestPage = contestRepo.findAll(spec, pagingSearch.toPageable());

        log.debug("contestPage: {}", contestPage);

        List<ContestResponse> contestResponses = contestPage.getContent().stream()
                .map(ContestMapper::toDto)
                .toList();

        log.debug("contestResponses: {}", contestResponses);

        return contestResponses;
    }

    @Override
    public ContestDetailResponse getContestDetail(Integer contestId) {
        Contest contest = contestRepo.findById(contestId)
                .orElseThrow(() -> new ResourceNotFoundException("Contest", "id", String.valueOf(contestId)));

        ContestDetailResponse contestDetailResponse = ContestMapper.toDetailDto(contest);

        log.debug("contestDetailResponse: {}", contestDetailResponse);

        return contestDetailResponse;
    }



}
