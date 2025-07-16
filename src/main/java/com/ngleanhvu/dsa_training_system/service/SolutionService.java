package com.ngleanhvu.dsa_training_system.service;

import com.ngleanhvu.dsa_training_system.dto.request.DiscussCreateRequest;
import com.ngleanhvu.dsa_training_system.dto.request.DiscussFilterRequest;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import com.ngleanhvu.dsa_training_system.dto.response.SolutionResponse;

import java.util.List;

public interface SolutionService {
    void createSolution(Integer problemId, DiscussCreateRequest discussCreateRequest);
    List<SolutionResponse> getSolutions(DiscussFilterRequest discussFilterRequest, PagingSearch pagingSearch);
}
