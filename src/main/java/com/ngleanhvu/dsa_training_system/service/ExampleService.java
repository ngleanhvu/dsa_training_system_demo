package com.ngleanhvu.dsa_training_system.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ngleanhvu.dsa_training_system.dto.request.ExampleCreateRequest;
import com.ngleanhvu.dsa_training_system.dto.request.ExampleUpdateInfoRequest;
import com.ngleanhvu.dsa_training_system.dto.response.ExampleResponse;
import com.ngleanhvu.dsa_training_system.entity.Example;
import com.ngleanhvu.dsa_training_system.entity.Problem;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ExampleService {
    Example createExample(ExampleCreateRequest request, Problem problem) throws JsonProcessingException;
    void createExamples(List<ExampleCreateRequest> requests, int problemId) throws JsonProcessingException;
    List<ExampleResponse> getExamples(int problemId);
    void updateExampleImages(Integer exampleId, List<MultipartFile> files);
    void updateExampleInfo(Integer exampleId, ExampleUpdateInfoRequest request);
}
