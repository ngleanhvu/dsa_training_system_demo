package com.ngleanhvu.dsa_training_system.dto.response;

import com.ngleanhvu.dsa_training_system.entity.ProgrammingLanguage;
import com.ngleanhvu.dsa_training_system.entity.SubmissionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BasicResultSubmissionResponse {
    private String message;
    private SubmissionStatus status;
    private int submissionId;
    private String sourceCode;
    private LocalDateTime timestamp;
    private double memory;
    private double time;
    private String programmingLanguage;
    private int problemId;
}
