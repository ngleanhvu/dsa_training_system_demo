package com.ngleanhvu.dsa_training_system.service;

import com.ngleanhvu.dsa_training_system.dto.request.CommentRequest;
import com.ngleanhvu.dsa_training_system.dto.request.DiscussCreateRequest;

public interface CommentService {
    void createComment(CommentRequest commentRequest,
                       Integer discussId);

}
