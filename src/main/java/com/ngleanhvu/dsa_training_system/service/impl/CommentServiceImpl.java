package com.ngleanhvu.dsa_training_system.service.impl;

import com.ngleanhvu.dsa_training_system.dto.request.CommentRequest;
import com.ngleanhvu.dsa_training_system.dto.request.CommentUpdateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.CommentResponse;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import com.ngleanhvu.dsa_training_system.entity.Comment;
import com.ngleanhvu.dsa_training_system.entity.CommentVote;
import com.ngleanhvu.dsa_training_system.entity.Discuss;
import com.ngleanhvu.dsa_training_system.entity.User;
import com.ngleanhvu.dsa_training_system.exception.PermissionException;
import com.ngleanhvu.dsa_training_system.exception.ResourceNotFoundException;
import com.ngleanhvu.dsa_training_system.mappter.CommentMapper;
import com.ngleanhvu.dsa_training_system.repo.CommentRepo;
import com.ngleanhvu.dsa_training_system.repo.CommentVoteRepo;
import com.ngleanhvu.dsa_training_system.repo.DiscussRepo;
import com.ngleanhvu.dsa_training_system.repo.UserRepo;
import com.ngleanhvu.dsa_training_system.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepo commentRepo;
    private final DiscussRepo discussRepo;
    private final CommentVoteRepo commentVoteRepo;
    private final UserRepo userRepo;

    @Transactional
    @Override
    public void createComment(CommentRequest commentRequest,
                              Integer discussId) {

        User user = userRepo.findById(commentRequest.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", commentRequest.getUserId()));

        Discuss discuss = discussRepo.findById(discussId)
                .orElseThrow(() -> new ResourceNotFoundException("Discuss", "id", String.valueOf(discussId)));

        Comment comment = Comment.builder()
                .discuss(discuss)
                .status(1)
                .commentCount(0)
                .upVotes(0)
                .content(commentRequest.getContent())
                .user(user)
                .build();

        if (commentRequest.getParentCommentId() != null) {
            Comment parentComment = commentRepo.findById(commentRequest.getParentCommentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", String.valueOf(commentRequest.getParentCommentId())));

            comment.setParent(parentComment);
            parentComment.setCommentCount(parentComment.getCommentCount() + 1);
        }

        discuss.setCommentCount(comment.getCommentCount() + 1);
        commentRepo.save(comment);
    }

    @Override
    public List<CommentResponse> getCommentsByDiscuss(Integer discussId, PagingSearch pagingSearch) {
        log.info("discussId: {}", discussId);
        Page<Comment> comments = commentRepo.findCommentsByDiscuss(discussId, pagingSearch.toPageable());
        log.info("comments: {}", comments);
        List<CommentResponse> commentResponses = comments.stream()
                .map(CommentMapper::toDto)
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
                .map(CommentMapper::toDto)
                .toList();
        log.info("commentResponses: {}", commentResponses);
        return commentResponses;
    }

    @Transactional
    @Override
    public void toggleVote(String userId, Integer commentId) {
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", String.valueOf(commentId)));

        Optional<CommentVote> commentVoteOptional = commentVoteRepo.findById(commentId);

        if (commentVoteOptional.isPresent()) {
            comment.setUpVotes(Math.max(0, comment.getUpVotes() - 1));
            commentVoteRepo.delete(commentVoteOptional.get());
        } else {

            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

            CommentVote commentVote = new CommentVote();
            commentVote.setComment(comment);
            commentVote.setUser(user);
            commentVote.setStatus(1);
            commentVoteRepo.save(commentVote);
            comment.setCommentCount(comment.getCommentCount() + 1);
        }
        commentRepo.save(comment);
    }

    @Transactional
    @Override
    public void updateComment(Integer commentId, CommentUpdateRequest commentUpdateRequest) {

        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", String.valueOf(commentId)));

        if (!comment.getUser().getUserId().equals(commentUpdateRequest.getUserId())) {
            throw new PermissionException("User",commentUpdateRequest.getUserId());
        }

        comment.setContent(commentUpdateRequest.getContent());
        commentRepo.save(comment);
    }

    @Transactional
    @Override
    public void deleteComment(Integer commentId, String userId) {

        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", String.valueOf(commentId)));

        if (!comment.getUser().getUserId().equals(userId)) {
            throw new PermissionException("User",userId);
        }

        commentRepo.deleteById(commentId);
    }

}
