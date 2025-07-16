package com.ngleanhvu.dsa_training_system.service;

import com.ngleanhvu.dsa_training_system.dto.request.DiscussCreateRequest;
import com.ngleanhvu.dsa_training_system.dto.request.DiscussFilterRequest;
import com.ngleanhvu.dsa_training_system.dto.request.DiscussUpdateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.DiscussResponse;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import com.ngleanhvu.dsa_training_system.entity.Discuss;
import com.ngleanhvu.dsa_training_system.entity.Problem;

import java.util.List;

public interface DiscussService {
    void createDiscuss(DiscussCreateRequest request);
    List<DiscussResponse> getDiscusses(DiscussFilterRequest discussFilterRequest, PagingSearch pagingSearch);
    void toggleVote(String userId, Integer discussId);
    void deleteDiscuss(Integer discussId);
    void updateDiscuss(Integer discussId, DiscussUpdateRequest request);
}
