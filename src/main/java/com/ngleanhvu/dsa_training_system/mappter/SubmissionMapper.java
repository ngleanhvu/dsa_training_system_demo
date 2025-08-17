package com.ngleanhvu.dsa_training_system.mappter;

import com.ngleanhvu.dsa_training_system.dto.response.BasicResultSubmissionResponse;
import com.ngleanhvu.dsa_training_system.dto.response.SubmissionResponse;
import com.ngleanhvu.dsa_training_system.entity.Submission;

public class SubmissionMapper {
    public static BasicResultSubmissionResponse toDto(Submission s) {
        return BasicResultSubmissionResponse.builder()
                .submissionId(s.getSubmissionId())
                .time(s.getRuntimeMs())
                .timestamp(s.getCreatedAt())
                .memory(s.getMemoryKb())
                .programmingLanguage(s.getProgrammingLanguage().getName())
                .message(s.getErrorMessage())
                .sourceCode(s.getCode())
                .status(s.getSubmissionStatus())
                .submissionId(s.getSubmissionId())
                .problemId(s.getProblem().getProblemId())
                .build();
    }

}
