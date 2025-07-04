package com.ngleanhvu.dsa_training_system.elasticsearch;

import com.ngleanhvu.dsa_training_system.entity.SubmissionStatus;
import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class ProblemSearchRequest {
    private String title;
    private String filterType;
    private Map<String, List<Integer>> topicIds;
    private Map<String, List<Integer>> difficultyIds;
    private List<SubmissionStatus> status;
}
