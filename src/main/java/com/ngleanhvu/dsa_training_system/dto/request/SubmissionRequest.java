package com.ngleanhvu.dsa_training_system.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmissionRequest {
    @NotNull(message = "ID bài toán không được để trống")
    private Integer problemId;
    @NotNull(message = "Vui lòng nhập source code")
    private String sourceCode;
    @NotNull(message = "Vui lòng chọn ngôn ngữ lập trình")
    private Integer languageId;
    private String stdin;
    private Integer contestId;
    private String userId;
}
