package com.ngleanhvu.dsa_training_system.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ExampleUpdateInfoRequest {
    private String input;
    private String output;
    private String explanation;
    private List<MultipartFile> files;
}
