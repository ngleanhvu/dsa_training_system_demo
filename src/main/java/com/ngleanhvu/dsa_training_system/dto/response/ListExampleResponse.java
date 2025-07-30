package com.ngleanhvu.dsa_training_system.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ListExampleResponse {
    private int page;
    private int totalPages;
    private List<ExampleResponse> examples;
}
