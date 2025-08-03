package com.ngleanhvu.dsa_training_system.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class DiscussCreateRequest {
    private String userId;
    @NotEmpty(message = "Vui lòng nhập tiêu đề")
    @NotNull(message = "Vui lòng nhập tiêu đề")
    private String title;
    @NotEmpty(message = "Vui lòng nhập mô tả")
    @NotNull(message = "Vui lòng nhập mô tả")
    private String content;
    private List<Integer> tagIds;
}
