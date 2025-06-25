package com.ngleanhvu.dsa_training_system.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ngleanhvu.dsa_training_system.dto.request.ExampleCreateRequest;
import com.ngleanhvu.dsa_training_system.entity.Example;

import java.util.List;

public interface ExampleService {
    Example createExample(ExampleCreateRequest request, int problemId) throws JsonProcessingException;
    void createExamples(List<ExampleCreateRequest> requests, int problemId) throws JsonProcessingException;
}
