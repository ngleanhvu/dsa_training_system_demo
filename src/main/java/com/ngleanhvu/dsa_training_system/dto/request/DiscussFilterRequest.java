package com.ngleanhvu.dsa_training_system.dto.request;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class DiscussFilterRequest {
    private Integer problemId;
    private String keyword;
    private RangeRequest<LocalDate> timestamp;
    private List<Integer> tagIds;
}
