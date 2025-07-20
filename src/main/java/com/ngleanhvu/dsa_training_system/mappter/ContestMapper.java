package com.ngleanhvu.dsa_training_system.mappter;

import com.ngleanhvu.dsa_training_system.dto.response.ContestDetailResponse;
import com.ngleanhvu.dsa_training_system.dto.response.ContestResponse;
import com.ngleanhvu.dsa_training_system.entity.Contest;
public class ContestMapper {
    public static ContestResponse toDto(Contest contest) {
        return ContestResponse.builder()
                .title(contest.getTitle())
                .contestId(contest.getContestId())
                .startTime(contest.getStartTime())
                .build();
    }

    public static ContestDetailResponse toDetailDto(Contest contest) {
        return ContestDetailResponse.builder()
                .contestId(contest.getContestId())
                .title(contest.getTitle())
                .description(contest.getDescription())
                .durationMinutes(contest.getDurationMinutes())
                .startTime(contest.getStartTime())
                .endTime(contest.getEndTime())
                .build();
    }
}
