package com.ngleanhvu.dsa_training_system.service;

import com.ngleanhvu.dsa_training_system.dto.request.DiscussCreateRequest;
import com.ngleanhvu.dsa_training_system.dto.request.DiscussFilterRequest;
import com.ngleanhvu.dsa_training_system.dto.request.DiscussUpdateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.*;

import java.util.List;

public interface DiscussService {
    void createDiscuss(DiscussCreateRequest request);
    ListDiscussResponse getDiscusses(DiscussFilterRequest discussFilterRequest, PagingSearch pagingSearch);
    DiscussDetailResponse getDiscussById(Integer discussId);
    void toggleVote(String userId, Integer discussId);
    void deleteDiscuss(String userId, Integer discussId);
    void updateDiscuss(Integer discussId, DiscussUpdateRequest request);
    DiscussDetailResponse getDiscussForUpdate(Integer discussId);
    ListDiscussResponse getDiscussesWithUser(DiscussFilterRequest discussFilterRequest,
                                             String userId,
                                             PagingSearch pagingSearch);
    DiscussDetailResponse getDiscussDetail(Integer discussId,
                                           String userId);
}
