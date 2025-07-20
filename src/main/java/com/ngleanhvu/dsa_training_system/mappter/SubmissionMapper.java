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
                .programmingLanguage(s.getProgrammingLanguage())
                .message(s.getErrorMessage())
                .status(s.getSubmissionStatus())
                .submissionId(s.getSubmissionId())
                .build();
    }

    public static SubmissionResponse toSubmissionDto(Sub) {}
}
