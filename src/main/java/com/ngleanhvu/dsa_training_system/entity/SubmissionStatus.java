package com.ngleanhvu.dsa_training_system.entity;

import lombok.Getter;

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

}


