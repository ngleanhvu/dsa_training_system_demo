package com.ngleanhvu.dsa_training_system.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ContestParticipantResultResponse {
    private Integer problemId;
    private boolean solved;
    private Integer wrongCount;
    private LocalDateTime solvedTime;
}
