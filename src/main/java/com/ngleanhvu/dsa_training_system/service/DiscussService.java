package com.ngleanhvu.dsa_training_system.service;

import com.ngleanhvu.dsa_training_system.dto.request.DiscussCreateRequest;
import com.ngleanhvu.dsa_training_system.entity.Problem;

public interface DiscussService {
    void createDiscuss(DiscussCreateRequest request);
}
