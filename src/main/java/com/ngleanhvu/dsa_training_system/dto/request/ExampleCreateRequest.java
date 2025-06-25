package com.ngleanhvu.dsa_training_system.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ExampleCreateRequest {
    @NotNull
    private String input;
    @NotNull
    private String output;
    private String explanation;
    private List<MultipartFile> files;
}
