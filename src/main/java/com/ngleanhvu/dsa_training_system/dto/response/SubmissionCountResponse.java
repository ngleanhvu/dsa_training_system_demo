package com.ngleanhvu.dsa_training_system.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class SubmissionCountResponse {
    private LocalDate timestamp;
    private Long count;
}
