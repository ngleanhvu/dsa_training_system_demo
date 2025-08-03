package com.ngleanhvu.dsa_training_system.service;

import com.ngleanhvu.dsa_training_system.dto.request.DiscussCreateRequest;
import com.ngleanhvu.dsa_training_system.dto.request.DiscussFilterRequest;
import com.ngleanhvu.dsa_training_system.dto.request.DiscussUpdateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.DiscussDetailResponse;
import com.ngleanhvu.dsa_training_system.dto.response.DiscussResponse;
import com.ngleanhvu.dsa_training_system.dto.response.ListDiscussResponse;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;

import java.util.List;

public interface DiscussService {
    void createDiscuss(DiscussCreateRequest request);
    ListDiscussResponse getDiscusses(DiscussFilterRequest discussFilterRequest, PagingSearch pagingSearch);
    DiscussDetailResponse getDiscussById(Integer discussId);
    void toggleVote(String userId, Integer discussId);
    void deleteDiscuss(String userId, Integer discussId);
    void updateDiscuss(Integer discussId, DiscussUpdateRequest request);
}
