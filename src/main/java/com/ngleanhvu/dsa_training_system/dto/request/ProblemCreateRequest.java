package com.ngleanhvu.dsa_training_system.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ProblemCreateRequest {
    @NotNull(message = "Vui lòng nhập tiêu đề")
    private String title;
    @NotNull(message = "Vui lòng chọn độ khó")
    private int difficultId;
    @NotEmpty(message = "Vui lòng chọn chủ đề")
    @NotEmpty(message = "Vui lòng chọn chủ đề")
    private List<Integer> topicIds;
    @NotNull(message = "Vui lòng nhập mô tả")
    private String description;
    private String constraints;
    private List<String> hints;
    @Min(value = 1, message = "Giới hạn thời gian phải lớn hơn 0")
    private int timeLimit = 1000;
    @Min(value = 1, message = "Giới hạn bộ nhớ phải lớn hơn 0")
    private int memoryLimit = 256;
    private boolean isPublic = false;
}
