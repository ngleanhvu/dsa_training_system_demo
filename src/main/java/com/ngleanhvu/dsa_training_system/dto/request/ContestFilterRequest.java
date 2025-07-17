package com.ngleanhvu.dsa_training_system.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ContestFilterRequest {
    private Integer contestId;
    private String keyword;
    private RangeRequest<LocalDate> timestamp;
    private String status;
}
