package com.ngleanhvu.dsa_training_system.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ProblemUpdateRequest {
    @NotNull(message = "Vui lòng nhập tiêu đề")
    private String title;
    @NotNull(message = "Vui lòng chọn độ khó")
    private Integer difficultId;
    @NotNull(message = "Vui lòng chọn chủ đề")
    private List<Integer> topicIds;
    @NotNull(message = "Vui lòng nhập mô tả")
    private String description;
    private String constraints;
    private List<String> hints;
    private Integer timeLimit = 1000;
    private Integer memoryLimit = 256;
}
