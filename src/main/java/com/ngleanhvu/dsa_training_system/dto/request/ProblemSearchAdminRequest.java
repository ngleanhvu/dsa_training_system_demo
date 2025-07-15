package com.ngleanhvu.dsa_training_system.dto.request;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;


@Data
public class ProblemSearchAdminRequest {
    private String title;
    private List<Integer> topicIds;
    private Integer difficultyId;
    private RangeRequest<LocalDate> publishDate;
    private RangeRequest<Integer> questionIdRange;
}
