package com.ngleanhvu.dsa_training_system.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
public class ExampleUpdateRequest {
    @NotNull(message = "Vui lòng nhập input")
    @NotEmpty(message = "Vui lòng nhập input")
    private String input;
    @NotNull(message = "Vui lòng nhập output")
    @NotEmpty(message = "Vui lòng nhập output")
    private String output;
    private String explanation;
    private List<MultipartFile> files;
}
