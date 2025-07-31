package com.ngleanhvu.dsa_training_system.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ngleanhvu.dsa_training_system.dto.request.ExampleCreateRequest;
import com.ngleanhvu.dsa_training_system.dto.request.ExampleUpdateInfoRequest;
import com.ngleanhvu.dsa_training_system.dto.request.ExampleUpdateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.ExampleResponse;
import com.ngleanhvu.dsa_training_system.dto.response.ListExampleResponse;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import com.ngleanhvu.dsa_training_system.entity.Example;
import com.ngleanhvu.dsa_training_system.entity.Problem;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ExampleService {
    Example createExample(ExampleCreateRequest request, Problem problem, List<MultipartFile> files) throws JsonProcessingException;
    void createExample(ExampleCreateRequest requests, int problemId, List<MultipartFile> files) throws JsonProcessingException;
    ListExampleResponse getExamples(Integer problemId, PagingSearch pagingSearch);
    void updateExample(Integer exampleId, ExampleUpdateRequest request);
    void deleteExample(Integer exampleId);
    ExampleResponse getExampleById(Integer exampleId);
}
