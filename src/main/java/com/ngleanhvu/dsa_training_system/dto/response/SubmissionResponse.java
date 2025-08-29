package com.ngleanhvu.dsa_training_system.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ngleanhvu.dsa_training_system.entity.SubmissionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionResponse {
    private String expectOutput;
    private String input;
    private RunResult run;
    private SubmissionStatus status;
    private int testCaseId;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RunResult {
        private String stdout;
        private String stderr;
        private Integer code;
        private String signal;
        private String message;
        private String status;

        @JsonProperty("cpu_time")
        private Integer cpuTime;

        @JsonProperty("wall_time")
        private Integer wallTime;

        private double memory;
    }
}
