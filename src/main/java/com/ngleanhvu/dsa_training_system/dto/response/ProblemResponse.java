package com.ngleanhvu.dsa_training_system.dto.response;

import com.ngleanhvu.dsa_training_system.entity.Difficulty;
import com.ngleanhvu.dsa_training_system.entity.Topic;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProblemResponse {
    private Integer problemId;
    private String title;
    private Difficulty difficulty;
    private List<Topic> topics;
}
