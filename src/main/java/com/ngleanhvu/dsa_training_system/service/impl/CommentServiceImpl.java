package com.ngleanhvu.dsa_training_system.service.impl;

import com.ngleanhvu.dsa_training_system.dto.request.CommentRequest;
import com.ngleanhvu.dsa_training_system.entity.Comment;
import com.ngleanhvu.dsa_training_system.entity.Discuss;
import com.ngleanhvu.dsa_training_system.exception.ResourceNotFoundException;
import com.ngleanhvu.dsa_training_system.repo.CommentRepo;
import com.ngleanhvu.dsa_training_system.repo.DiscussRepo;
import com.ngleanhvu.dsa_training_system.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
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
}
