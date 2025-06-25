package com.ngleanhvu.dsa_training_system.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ngleanhvu.dsa_training_system.dto.request.ExampleCreateRequest;

import java.util.List;

public interface ExampleService {
    void createExample(ExampleCreateRequest request, int problemId) throws JsonProcessingException;
    void createExamples(List<ExampleCreateRequest> requests, int problemId) throws JsonProcessingException;
}
