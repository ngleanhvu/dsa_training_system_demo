package com.ngleanhvu.dsa_training_system.service;

import com.ngleanhvu.dsa_training_system.dto.request.ContestCreateRequest;
import com.ngleanhvu.dsa_training_system.dto.request.ContestFilterRequest;
import com.ngleanhvu.dsa_training_system.dto.request.ContestUpdateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.ContestDetailResponse;
import com.ngleanhvu.dsa_training_system.dto.response.ContestResponse;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ContestService {
    void createContest(ContestCreateRequest contest);
    void updateContest(Integer contestId, ContestUpdateRequest contest);
    void deleteContest(Integer contestId);
    List<ContestResponse> getContests(ContestFilterRequest contestFilterRequest, PagingSearch pagingSearch);
    ContestDetailResponse getContestDetail(Integer contestId);
}
