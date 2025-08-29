package com.ngleanhvu.dsa_training_system.service.impl;

import com.ngleanhvu.dsa_training_system.dto.request.CommentRequest;
import com.ngleanhvu.dsa_training_system.dto.request.CommentUpdateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.CommentResponse;
import com.ngleanhvu.dsa_training_system.dto.response.ListCommentResponse;
import com.ngleanhvu.dsa_training_system.dto.response.NotificationResponse;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import com.ngleanhvu.dsa_training_system.entity.*;
import com.ngleanhvu.dsa_training_system.exception.PermissionException;
import com.ngleanhvu.dsa_training_system.exception.ResourceNotFoundException;
import com.ngleanhvu.dsa_training_system.mappter.CommentMapper;
import com.ngleanhvu.dsa_training_system.repo.*;
import com.ngleanhvu.dsa_training_system.service.CommentService;
import com.ngleanhvu.dsa_training_system.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

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
    private final NotificationService notificationService;

    @Transactional
    @Override
    public void createComment(CommentRequest request, Integer discussId) {
        User user = getUserOrThrow(request.getUserId());
        Discuss discuss = getDiscussOrThrow(discussId);

        Comment comment = Comment.builder()
                .discuss(discuss)
                .status(1)
                .commentCount(0)
                .upVotes(0)
                .content(request.getContent())
                .user(user)
                .build();

        String notificationEmail;
        String contentSuffix;

        if (request.getParentCommentId() != null) {
            Comment parent = getCommentOrThrow(request.getParentCommentId());
            comment.setParent(parent);
            parent.setCommentCount(parent.getCommentCount() + 1);
            commentRepo.save(parent);

            notificationEmail = parent.getUser().getEmail();
            contentSuffix = " have replied your comment";
        } else {
            notificationEmail = discuss.getUser().getEmail();
            contentSuffix = " have comment your discuss";
        }

        discuss.setCommentCount(discuss.getCommentCount() + 1);
        commentRepo.save(comment);
        sendNotification(user.getDisplayName(), contentSuffix, notificationEmail);
    }

    @Transactional
    @Override
    public void createCommentForProblem(CommentRequest request, Integer problemId) {
        User user = getUserOrThrow(request.getUserId());
        Problem problem = getProblemOrThrow(problemId);

        Comment comment = Comment.builder()
                .content(request.getContent())
                .user(user)
                .status(1)
                .commentCount(0)
                .upVotes(0)
                .build();

        if (request.getParentCommentId() != null) {
            Comment parent = getCommentOrThrow(request.getParentCommentId());
            comment.setParent(parent);
            parent.setCommentCount(parent.getCommentCount() + 1);
            commentRepo.save(parent);

            sendNotification(user.getDisplayName(), " have replied your comment", parent.getUser().getEmail());
        }

        commentRepo.save(comment);

        if (request.getParentCommentId() == null) {
            ProblemComment problemComment = new ProblemComment();
            problemComment.setComment(comment);
            problemComment.setProblem(problem);
            problemCommentRepo.save(problemComment);
        }
    }

    @Override
    public ListCommentResponse getCommentsByDiscuss(Integer discussId, PagingSearch paging) {
        Page<Comment> comments = commentRepo.findCommentsByDiscuss(discussId, paging.toPageable());
        return buildListCommentResponse(comments);
    }

    @Override
    public ListCommentResponse getCommentsByDiscussWithCredential(Integer discussId, String userId, PagingSearch paging) {
        Page<Object[]> comments = commentRepo.findCommentByDiscussWithCredential(discussId, userId, paging.toPageable());
        List<CommentResponse> responses = comments.getContent().stream()
                .map(this::mapObjectArrayToCommentResponse)
                .toList();

        return ListCommentResponse.builder()
                .comments(responses)
                .totalPages(comments.getTotalPages())
                .page(comments.getNumber() + 1)
                .build();
    }

    @Override
    public ListCommentResponse getCommentsByProblem(Integer problemId, PagingSearch paging, String userId) {
        if (userId != null) {
            Page<Object[]> comments = commentRepo.findCommentByUser(userId, problemId, paging.toPageable());
            return buildListCommentResponseFromObjectArray(comments);
        } else {
            Page<Comment> commentPage = commentRepo.findCommentByProblem(problemId, paging.toPageable());
            return buildListCommentResponse(commentPage);
        }
    }

    @Override
    public ListCommentResponse getChildCommentsByParentComment(Integer parentCommentId, PagingSearch paging) {
        Page<Comment> comments = commentRepo.findCommentsByParentComment(parentCommentId, paging.toPageable());
        return buildListCommentResponse(comments);
    }

    @Override
    public ListCommentResponse getCommentsByParentCommentWithUser(Integer parentCommentId, String userId, PagingSearch paging) {
        if (userId != null) {
            Page<Object[]> comments = commentRepo.findCommentsByParentCommentWithUser(parentCommentId, userId, paging.toPageable());
            return buildListCommentResponseFromObjectArray(comments);
        } else {
            Page<Comment> commentPage = commentRepo.findCommentsByParentComment(parentCommentId, paging.toPageable());
            return buildListCommentResponse(commentPage);
        }
    }

    @Transactional
    @Override
    public void toggleVote(String userId, Integer commentId) {
        Comment comment = getCommentOrThrow(commentId);
        commentVoteRepo.findByCommentIdAndUserId(commentId, userId).ifPresentOrElse(vote -> {
            comment.setUpVotes(Math.max(0, comment.getUpVotes() - 1));
            commentVoteRepo.delete(vote);
        }, () -> {
            User user = getUserOrThrow(userId);
            CommentVote vote = new CommentVote();
            vote.setComment(comment);
            vote.setUser(user);
            vote.setStatus(1);
            commentVoteRepo.save(vote);
            comment.setUpVotes(comment.getUpVotes() + 1);
        });
        commentRepo.save(comment);
    }

    @Transactional
    @Override
    public void updateComment(Integer commentId, CommentUpdateRequest request) {
        Comment comment = getCommentOrThrow(commentId);
        checkPermission(comment.getUser().getUserId(), request.getUserId());
        comment.setContent(request.getContent());
        commentRepo.save(comment);
    }

    @Transactional
    @Override
    public void deleteComment(Integer commentId, String userId) {
        Comment comment = getCommentOrThrow(commentId);
        checkPermission(comment.getUser().getUserId(), userId);

        if (comment.getParent() != null) {
            Comment parent = comment.getParent();
            parent.setCommentCount(Math.max(0, parent.getCommentCount() - 1));
            commentRepo.save(parent);
        }

        if (comment.getDiscuss() != null) {
            Discuss discuss = comment.getDiscuss();
            discuss.setCommentCount(Math.max(0, discuss.getCommentCount() - 1));
            discussRepo.save(discuss);
        }

        commentRepo.deleteById(commentId);
    }

    // =================== PRIVATE HELPERS ===================

    private User getUserOrThrow(String userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    private Discuss getDiscussOrThrow(Integer discussId) {
        return discussRepo.findById(discussId)
                .orElseThrow(() -> new ResourceNotFoundException("Discuss", "id", discussId.toString()));
    }

    private Comment getCommentOrThrow(Integer commentId) {
        return commentRepo.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId.toString()));
    }

    private Problem getProblemOrThrow(Integer problemId) {
        return problemRepo.findById(problemId)
                .orElseThrow(() -> new ResourceNotFoundException("Problem", "id", problemId.toString()));
    }

    private void sendNotification(String displayName, String contentSuffix, String email) {
        NotificationResponse response = new NotificationResponse();
        response.setContent(displayName + contentSuffix);
        response.setRead(false);
        response.setSenderUserName(email);
        notificationService.sendPrivateNotification(response);
    }

    private ListCommentResponse buildListCommentResponse(Page<Comment> comments) {
        List<CommentResponse> responses = comments.stream()
                .map(CommentMapper::toDto)
                .toList();
        return ListCommentResponse.builder()
                .comments(responses)
                .totalPages(comments.getTotalPages())
                .page(comments.getNumber() + 1)
                .build();
    }

    private ListCommentResponse buildListCommentResponseFromObjectArray(Page<Object[]> comments) {
        List<CommentResponse> responses = comments.getContent().stream()
                .map(this::mapObjectArrayToCommentResponse)
                .toList();
        return ListCommentResponse.builder()
                .comments(responses)
                .totalPages(comments.getTotalPages())
                .page(comments.getNumber() + 1)
                .build();
    }

    private CommentResponse mapObjectArrayToCommentResponse(Object[] c) {
        CommentResponse response = new CommentResponse();
        response.setCommentId((Integer) c[0]);
        response.setContent((String) c[1]);
        response.setCommentCount((Integer) c[2]);
        response.setUpVotes((Integer) c[3]);
        response.setCreatedAt(convertToLocalDateTime(c[4]));
        response.setUserEmail((String) c[5]);
        response.setUserDisplayName((String) c[6]);
        response.setUserAvatar((String) c[7]);
        response.setIsUpVote((Long) c[8]);
        response.setViews(0);
        response.setDownVotes(0);
        return response;
    }

    private LocalDateTime convertToLocalDateTime(Object obj) {
        if (obj instanceof java.sql.Timestamp ts) {
            return ts.toLocalDateTime();
        } else if (obj instanceof LocalDateTime ldt) {
            return ldt;
        } else if (obj instanceof String s) {
            return LocalDateTime.parse(s);
        }
        return null;
    }

    private void checkPermission(String ownerId, String userId) {
        if (!ownerId.equals(userId)) {
            throw new PermissionException("User", userId);
        }
    }
}
