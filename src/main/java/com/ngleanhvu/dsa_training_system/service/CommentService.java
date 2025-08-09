package com.ngleanhvu.dsa_training_system.service;

import com.ngleanhvu.dsa_training_system.dto.request.CommentRequest;
import com.ngleanhvu.dsa_training_system.dto.request.CommentUpdateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.ListCommentResponse;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;

public interface CommentService {
    void createComment(CommentRequest commentRequest,
                       Integer discussId);

    void createCommentForProblem(CommentRequest commentRequest, Integer problemId);
    ListCommentResponse getCommentsByDiscuss(Integer discussId, PagingSearch pagingSearch);
    ListCommentResponse getCommentsByProblem(Integer problemId, PagingSearch pagingSearch, String userId);
    ListCommentResponse getChildCommentsByParentComment(Integer parentCommentId, PagingSearch pagingSearch);
    ListCommentResponse getCommentsByParentCommentWithUser(Integer parentCommentId, String userId, PagingSearch pagingSearch);

    void toggleVote(String userId, Integer commentId);
    void updateComment(Integer commentId, CommentUpdateRequest commentUpdateRequest);
    void deleteComment(Integer commentId, String userId);
}
