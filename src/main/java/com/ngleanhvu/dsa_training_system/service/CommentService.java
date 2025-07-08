package com.ngleanhvu.dsa_training_system.service;

import com.ngleanhvu.dsa_training_system.dto.request.CommentRequest;
import com.ngleanhvu.dsa_training_system.dto.request.DiscussCreateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.CommentResponse;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CommentService {
    void createComment(CommentRequest commentRequest,
                       Integer discussId);

    List<CommentResponse> getCommentsByDiscuss(Integer discussId, PagingSearch pagingSearch);

    List<CommentResponse> getChildCommentsByParentComment(Integer parentCommentId, PagingSearch pagingSearch);
}
