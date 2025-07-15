package com.ngleanhvu.dsa_training_system.service.impl;

import com.ngleanhvu.dsa_training_system.dto.request.ContestCreateRequest;
import com.ngleanhvu.dsa_training_system.entity.Contest;
import com.ngleanhvu.dsa_training_system.repo.ContestRepo;
import com.ngleanhvu.dsa_training_system.service.ContestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
                .durationMinutes(request.getDurationMinutes())
                .build();
        contestRepo.save(contestEntity);
    }


}
