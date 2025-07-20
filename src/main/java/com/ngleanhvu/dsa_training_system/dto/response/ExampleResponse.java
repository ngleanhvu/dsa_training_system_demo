package com.ngleanhvu.dsa_training_system.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ExampleResponse {
    private String input;
    private String output;
    private String explanation;
    private List<String> images;
}
