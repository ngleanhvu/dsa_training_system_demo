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
    private LocalDateTime timestamp;
    private int memory;
    private int time;
    private ProgrammingLanguage programmingLanguage;
}
