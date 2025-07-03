package com.ngleanhvu.dsa_training_system.dto.request;

import com.ngleanhvu.dsa_training_system.entity.Submission;
import com.ngleanhvu.dsa_training_system.entity.SubmissionStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProblemFilterRequest {
    private List<Integer> topicIds;
    private List<Integer> difficultyIds;
    private List<SubmissionStatus> status;
}
