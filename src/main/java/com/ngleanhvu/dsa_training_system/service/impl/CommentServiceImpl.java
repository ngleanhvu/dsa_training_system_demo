package com.ngleanhvu.dsa_training_system.service.impl;

import com.ngleanhvu.dsa_training_system.dto.request.CommentRequest;
import com.ngleanhvu.dsa_training_system.dto.request.CommentUpdateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.CommentResponse;
import com.ngleanhvu.dsa_training_system.dto.response.ListCommentResponse;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import com.ngleanhvu.dsa_training_system.entity.*;
import com.ngleanhvu.dsa_training_system.exception.PermissionException;
import com.ngleanhvu.dsa_training_system.exception.ResourceNotFoundException;
import com.ngleanhvu.dsa_training_system.mappter.CommentMapper;
import com.ngleanhvu.dsa_training_system.repo.*;
import com.ngleanhvu.dsa_training_system.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final ProblemRepo problemRepo;
    private final ProblemCommentRepo problemCommentRepo;

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

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void createCommentForProblem(CommentRequest commentRequest, Integer problemId) {
        log.info("Create comment for problem {}", problemId);

        User user = userRepo.findById(commentRequest.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", commentRequest.getUserId()));
        log.info("user = {}", user);
        Problem problem = problemRepo.findById(problemId)
                .orElseThrow(() -> new ResourceNotFoundException("Problem", "id", String.valueOf(problemId)));
        log.info("problem = {}", problem);
        Comment comment = Comment.builder()
                .content(commentRequest.getContent())
                .user(user)
                .upVotes(0)
                .status(1)
                .commentCount(0)
                .build();

        if (commentRequest.getParentCommentId() != null) {
            Comment parentComment = commentRepo.findCommentById(commentRequest.getParentCommentId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Comment", "id", String.valueOf(commentRequest.getParentCommentId())
                    ));
            comment.setParent(parentComment);
            parentComment.setCommentCount(parentComment.getCommentCount() + 1);
            commentRepo.save(parentComment);
        }

        // Lưu comment mới
        commentRepo.save(comment);

        log.info("comment = {}", comment);

       if (commentRequest.getParentCommentId() == null) {
           ProblemComment problemComment = new ProblemComment();
           problemComment.setComment(comment);
           problemComment.setProblem(problem);

           problemCommentRepo.save(problemComment);
       }


        log.info("Comment created successfully for problem {}", problemId);
    }


    @Override
    public ListCommentResponse getCommentsByDiscuss(Integer discussId, PagingSearch pagingSearch) {
        log.info("discussId: {}", discussId);
        Page<Comment> comments = commentRepo.findCommentsByDiscuss(discussId, pagingSearch.toPageable());
        log.info("comments: {}", comments);
        List<CommentResponse> commentResponses = comments.stream()
                .map(CommentMapper::toDto)
                .toList();
        log.info("commentResponses: {}", commentResponses);
        return ListCommentResponse.builder()
                .comments(commentResponses)
                .totalPages(comments.getTotalPages())
                .page(comments.getNumber()+1)
                .build();
    }

    @Override
    public ListCommentResponse getCommentsByProblem(Integer problemId, PagingSearch pagingSearch, String userId) {
        List<CommentResponse> commentResponses = new ArrayList<>();
        int page = 0;
        int totalPages = 1;
        log.info("problemId: {}", problemId);
        log.info("userId: {}", userId);

        if (userId != null) {
            Page<Object[]> comments = commentRepo.findCommentByUser(userId, problemId, pagingSearch.toPageable());
            page = comments.getNumber() + 1;
            log.info("page: {}", page);
            totalPages = comments.getTotalPages();
            log.info("totalPages: {}", totalPages);
            log.info("comments: {}", comments);
            commentResponses = comments.getContent()
                    .stream()
                    .map(c -> {
                        CommentResponse commentResponse = new CommentResponse();
                        Integer commentId = (Integer) c[0];
                        log.info("commentId: {}", commentId);
                        String content = (String) c[1];
                        log.info("content: {}", content);
                        Integer commentCount = (Integer) c[2];
                        log.info("commentCount: {}", commentCount);
                        Integer upVotes = (Integer) c[3];
                        log.info("upVotes: {}", upVotes);
                        Object createdAtObj = c[4];
                        LocalDateTime createdAt = null;

                        if (createdAtObj instanceof java.sql.Timestamp) {
                            createdAt = ((java.sql.Timestamp) createdAtObj).toLocalDateTime();
                        } else if (createdAtObj instanceof java.time.LocalDateTime) {
                            createdAt = (LocalDateTime) createdAtObj;
                        } else if (createdAtObj instanceof String) {
                            createdAt = LocalDateTime.parse((String) createdAtObj);
                        }
                        log.info("createdAt: {}", createdAt);
                        String userEmail = (String) c[5];
                        log.info("userEmail: {}", userEmail);
                        String userDisplayName = (String) c[6];
                        log.info("userDisplayName: {}", userDisplayName);
                        String userAvatar = (String) c[7];
                        log.info("userName: {}", userDisplayName);
                        log.info("c[8] class: {}", c[8] != null ? c[8].getClass().getName() : "null");
                        log.info("c[8] value: {}", c[8]);
                        Long isUpVote = (Long) c[8];
                        log.info("isUpVote: {}", isUpVote);

                        commentResponse.setCommentId(commentId);
                        commentResponse.setContent(content);
                        commentResponse.setCommentCount(commentCount);
                        commentResponse.setUpVotes(upVotes);
                        commentResponse.setUserEmail(userEmail);
                        commentResponse.setUserDisplayName(userDisplayName);
                        commentResponse.setUserAvatar(userAvatar);
                        commentResponse.setIsUpVote(isUpVote);
                        commentResponse.setViews(0);
                        commentResponse.setDownVotes(0);
                        commentResponse.setCreatedAt(createdAt);
                        log.info("commentResponse {}", commentResponse);
                        return commentResponse;

                    })
                    .toList();

        } else {
            Page<Comment> commentPage = commentRepo.findCommentByProblem(problemId, pagingSearch.toPageable());
            page = commentPage.getNumber() + 1;
            totalPages = commentPage.getTotalPages();
            commentResponses = commentPage.getContent()
                    .stream()
                    .map(CommentMapper::toDto)
                    .toList();
        }


        log.info("commentResponses: {}", commentResponses);
        return ListCommentResponse.builder()
                .comments(commentResponses)
                .totalPages(totalPages)
                .page(page)
                .build();
    }

    @Override
    public ListCommentResponse getChildCommentsByParentComment(Integer parentCommentId, PagingSearch pagingSearch) {
        log.info("parentCommentId: {}", parentCommentId);
        Page<Comment> comments = commentRepo.findCommentsByParentComment(parentCommentId, pagingSearch.toPageable());
        log.info("comments: {}", comments);
        List<CommentResponse> commentResponses = comments.stream()
                .map(CommentMapper::toDto)
                .toList();
        log.info("commentResponses: {}", commentResponses);
        return ListCommentResponse.builder()
                .comments(commentResponses)
                .totalPages(comments.getTotalPages())
                .page(comments.getNumber()+1)
                .build();
    }

    @Override
    public ListCommentResponse getCommentsByParentCommentWithUser(Integer parentCommentId, String userId, PagingSearch pagingSearch) {
        List<CommentResponse> commentResponses = new ArrayList<>();
        int page = 0;
        int totalPages = 1;
        log.info("parentCommentId: {}", parentCommentId);
        log.info("userId: {}", userId);

        if (userId != null) {
            Page<Object[]> comments = commentRepo.findCommentsByParentCommentWithUser(parentCommentId,userId, pagingSearch.toPageable());
            page = comments.getNumber() + 1;
            log.info("page: {}", page);
            totalPages = comments.getTotalPages();
            log.info("totalPages: {}", totalPages);
            log.info("comments: {}", comments);
            commentResponses = comments.getContent()
                    .stream()
                    .map(c -> {
                        CommentResponse commentResponse = new CommentResponse();
                        Integer commentId = (Integer) c[0];
                        log.info("commentId: {}", commentId);
                        String content = (String) c[1];
                        log.info("content: {}", content);
                        Integer commentCount = (Integer) c[2];
                        log.info("commentCount: {}", commentCount);
                        Integer upVotes = (Integer) c[3];
                        log.info("upVotes: {}", upVotes);
                        Object createdAtObj = c[4];
                        LocalDateTime createdAt = null;

                        if (createdAtObj instanceof java.sql.Timestamp) {
                            createdAt = ((java.sql.Timestamp) createdAtObj).toLocalDateTime();
                        } else if (createdAtObj instanceof java.time.LocalDateTime) {
                            createdAt = (LocalDateTime) createdAtObj;
                        } else if (createdAtObj instanceof String) {
                            createdAt = LocalDateTime.parse((String) createdAtObj);
                        }
                        log.info("createdAt: {}", createdAt);
                        String userEmail = (String) c[5];
                        log.info("userEmail: {}", userEmail);
                        String userDisplayName = (String) c[6];
                        log.info("userDisplayName: {}", userDisplayName);
                        String userAvatar = (String) c[7];
                        log.info("userName: {}", userDisplayName);
                        log.info("c[8] class: {}", c[8] != null ? c[8].getClass().getName() : "null");
                        log.info("c[8] value: {}", c[8]);
                        Long isUpVote = (Long) c[8];
                        log.info("isUpVote: {}", isUpVote);

                        commentResponse.setCommentId(commentId);
                        log.info("commentResponse: {}", commentResponse);
                        commentResponse.setContent(content);
                        log.info("commentResponse: {}", commentResponse);
                        commentResponse.setCommentCount(commentCount);
                        log.info("commentResponse: {}", commentResponse);
                        commentResponse.setUpVotes(upVotes);
                        log.info("commentResponse: {}", commentResponse);
                        commentResponse.setUserEmail(userEmail);
                        log.info("commentResponse: {}", commentResponse);
                        commentResponse.setUserDisplayName(userDisplayName);
                        log.info("commentResponse: {}", commentResponse);
                        commentResponse.setUserAvatar(userAvatar);
                        log.info("commentResponse: {}", commentResponse);
                        commentResponse.setIsUpVote(isUpVote);
                        log.info("commentResponse: {}", commentResponse);
                        commentResponse.setViews(0);
                        commentResponse.setDownVotes(0);
                        commentResponse.setCreatedAt(createdAt);
                        log.info("commentResponse {}", commentResponse);
                        return commentResponse;

                    })
                    .toList();

        } else {
            Page<Comment> commentPage = commentRepo.findCommentsByParentComment(parentCommentId, pagingSearch.toPageable());
            page = commentPage.getNumber() + 1;
            totalPages = commentPage.getTotalPages();
            commentResponses = commentPage.getContent()
                    .stream()
                    .map(CommentMapper::toDto)
                    .toList();
        }


        log.info("commentResponses: {}", commentResponses);
        return ListCommentResponse.builder()
                .comments(commentResponses)
                .totalPages(totalPages)
                .page(page)
                .build();
    }

    @Transactional
    @Override
    public void toggleVote(String userId, Integer commentId) {
        Comment comment = commentRepo.findCommentById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", String.valueOf(commentId)));
        log.info("comment from repo = {}", comment);
        Optional<CommentVote> commentVoteOptional = commentVoteRepo.findByCommentIdAndUserId(commentId, userId);
        log.info("commentVoteOptional = {}", commentVoteOptional);

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
            comment.setUpVotes(comment.getUpVotes() + 1);
        }
        commentRepo.save(comment);
    }

    @Transactional
    @Override
    public void updateComment(Integer commentId, CommentUpdateRequest commentUpdateRequest) {

        Comment comment = commentRepo.findCommentById(commentId)
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

        Comment comment = commentRepo.findCommentById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", String.valueOf(commentId)));

        if (!comment.getUser().getUserId().equals(userId)) {
            throw new PermissionException("User",userId);
        }

        if (comment.getParent() != null) {
            Comment parentComment = comment.getParent();
            parentComment.setCommentCount(Math.max(0, parentComment.getCommentCount() - 1));
            commentRepo.save(parentComment);
        }

        commentRepo.deleteById(commentId);
    }

}
