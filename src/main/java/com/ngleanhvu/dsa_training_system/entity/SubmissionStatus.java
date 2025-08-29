package com.ngleanhvu.dsa_training_system.entity;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public enum SubmissionStatus {
    PENDING("Pending"),
    ACCEPTED("Accepted"),
    WRONG_ANSWER("Wrong Answer"),
    TIME_LIMIT_EXCEEDED("Time Limit Exceeded"),
    MEMORY_LIMIT_EXCEEDED("Memory Limit Exceeded"),
    RUNTIME_ERROR("Runtime Error"),
    COMPILE_ERROR("Compile Error"),
    ERROR("Error"),
    NULL_RESPONSE("Null Response"),
    TIMEOUT("Timeout");

    private final String value;

    SubmissionStatus(String value) {
        this.value = value;
    }

    public static SubmissionStatus getSubmissionStatus(String value) {
        for (SubmissionStatus submissionStatus : SubmissionStatus.values()) {
            log.info("submissionStatus: {}", submissionStatus);
            if (submissionStatus.name().equalsIgnoreCase(value)) {
                return submissionStatus;
            }
        }
        return null;
    }

}


