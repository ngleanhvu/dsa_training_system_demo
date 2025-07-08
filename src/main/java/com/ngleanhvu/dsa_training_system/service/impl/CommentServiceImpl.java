package com.ngleanhvu.dsa_training_system.service.impl;

import com.ngleanhvu.dsa_training_system.dto.request.CommentRequest;
import com.ngleanhvu.dsa_training_system.dto.response.CommentResponse;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import com.ngleanhvu.dsa_training_system.entity.Comment;
import com.ngleanhvu.dsa_training_system.entity.Discuss;
import com.ngleanhvu.dsa_training_system.exception.ResourceNotFoundException;
import com.ngleanhvu.dsa_training_system.repo.CommentRepo;
import com.ngleanhvu.dsa_training_system.repo.DiscussRepo;
import com.ngleanhvu.dsa_training_system.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepo commentRepo;
    private final DiscussRepo discussRepo;

    @Transactional
    @Override
    public void createComment(CommentRequest commentRequest,
                              Integer discussId) {
        Discuss discuss = discussRepo.findById(discussId)
                .orElseThrow(() -> new ResourceNotFoundException("Discuss", "id", String.valueOf(discussId)));

        Comment comment = Comment.builder()
                .discuss(discuss)
                .status(1)
                .content(commentRequest.getContent())
                .build();

        if (commentRequest.getParentCommentId() != null) {
            Comment parentComment = commentRepo.findById(commentRequest.getParentCommentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", String.valueOf(commentRequest.getParentCommentId())));

            comment.setParent(parentComment);
        }

        commentRepo.save(comment);
    }

    @Override
    public List<CommentResponse> getCommentsByDiscuss(Integer discussId, PagingSearch pagingSearch) {
        log.info("discussId: {}", discussId);
        Page<Comment> comments = commentRepo.findCommentsByDiscuss(discussId, pagingSearch.toPageable());
        log.info("comments: {}", comments);
        List<CommentResponse> commentResponses = comments.stream()
                .map(c -> CommentResponse.builder()
                        .commentCount(c.getCommentCount())
                        .views(c.getViews())
                        .upVotes(c.getUpVotes())
                        .downVotes(c.getDownVotes())
                        .content(c.getContent())
                        .createdAt(c.getCreatedAt())
                        .userEmail(c.getUser().getEmail())
                        .userAvatar(c.getUser().getAvatar())
                        .userDisplayName(c.getUser().getDisplayName())
                        .build())
                .toList();
        log.info("commentResponses: {}", commentResponses);
        return commentResponses;
    }

    @Override
    public List<CommentResponse> getChildCommentsByParentComment(Integer parentCommentId, PagingSearch pagingSearch) {
        log.info("parentCommentId: {}", parentCommentId);
        Page<Comment> comments = commentRepo.findCommentsByParentComment(parentCommentId, pagingSearch.toPageable());
        log.info("comments: {}", comments);
        List<CommentResponse> commentResponses = comments.stream()
                .map(c -> CommentResponse.builder()
                        .commentCount(c.getCommentCount())
                        .views(c.getViews())
                        .upVotes(c.getUpVotes())
                        .downVotes(c.getDownVotes())
                        .content(c.getContent())
                        .createdAt(c.getCreatedAt())
                        .userEmail(c.getUser().getEmail())
                        .userAvatar(c.getUser().getAvatar())
                        .userDisplayName(c.getUser().getDisplayName())
                        .build())
                .toList();
        log.info("commentResponses: {}", commentResponses);
        return commentResponses;
    }


}
