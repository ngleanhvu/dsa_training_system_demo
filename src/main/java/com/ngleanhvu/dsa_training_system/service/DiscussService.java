package com.ngleanhvu.dsa_training_system.service;

import com.ngleanhvu.dsa_training_system.dto.request.DiscussCreateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.DiscussResponse;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import com.ngleanhvu.dsa_training_system.entity.Discuss;
import com.ngleanhvu.dsa_training_system.entity.Problem;

import java.util.List;

public interface DiscussService {
    void createDiscuss(DiscussCreateRequest request);
    List<DiscussResponse> getDiscusses(String keyword, PagingSearch pagingSearch);
}
