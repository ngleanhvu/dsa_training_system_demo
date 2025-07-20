package com.ngleanhvu.dsa_training_system.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ngleanhvu.dsa_training_system.dto.request.ProblemCreateRequest;
import com.ngleanhvu.dsa_training_system.dto.request.ProblemSearchAdminRequest;
import com.ngleanhvu.dsa_training_system.dto.request.ProblemSearchRequest;
import com.ngleanhvu.dsa_training_system.dto.request.ProblemUpdateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import com.ngleanhvu.dsa_training_system.dto.response.ProblemResponse;
import com.ngleanhvu.dsa_training_system.entity.Problem;

import java.util.List;

public interface ProblemService {
    void createProblem(ProblemCreateRequest request) throws JsonProcessingException;
    List<ProblemResponse> getProblems(ProblemSearchAdminRequest searchRequest, PagingSearch pagingSearch);
    void updateProblem(Integer problemId, ProblemUpdateRequest problemUpdateRequest) throws JsonProcessingException;
    void deleteProblem(Integer problemId);
    ProblemResponse getProblem(Integer problemId);
    void togglePublishProblem(Integer problemId) throws JsonProcessingException;
}
