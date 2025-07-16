package com.ngleanhvu.dsa_training_system.service;

import com.ngleanhvu.dsa_training_system.dto.request.DiscussCreateRequest;
import com.ngleanhvu.dsa_training_system.dto.request.DiscussFilterRequest;
import com.ngleanhvu.dsa_training_system.dto.response.DiscussResponse;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;

import java.util.List;

public interface SolutionService {
    void createSolution(Integer problemId, DiscussCreateRequest discussCreateRequest);
    List<DiscussResponse> getSolutions(DiscussFilterRequest discussFilterRequest, PagingSearch pagingSearch);
}
