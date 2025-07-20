package com.ngleanhvu.dsa_training_system.elasticsearch;

import com.ngleanhvu.dsa_training_system.dto.request.RangeRequest;
import com.ngleanhvu.dsa_training_system.entity.SubmissionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class ProblemSearchRequest {
    private String title;
    private String filterType;
    private Map<String, List<Integer>> topicIds;
    private Map<String, List<Integer>> difficultyIds;
    private RangeRequest<Double> questionIdRange;
    private RangeRequest<Double> acceptanceRateRange;
    private RangeRequest<String> publishedDateRange;
    private List<SubmissionStatus> status;
    private String userId;
}
