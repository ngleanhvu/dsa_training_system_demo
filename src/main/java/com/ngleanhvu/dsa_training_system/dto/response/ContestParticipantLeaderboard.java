package com.ngleanhvu.dsa_training_system.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ContestParticipantLeaderboard {
    private String email;
    private String displayName;
    private List<ContestParticipantResultResponse> contestParticipantResultResponseList;
}
