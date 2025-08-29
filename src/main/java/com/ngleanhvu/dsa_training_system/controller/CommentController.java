package com.ngleanhvu.dsa_training_system.controller;

import com.ngleanhvu.dsa_training_system.constant.ValueConst;
import com.ngleanhvu.dsa_training_system.dto.request.CommentRequest;
import com.ngleanhvu.dsa_training_system.dto.request.CommentUpdateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.ApiResponse;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import com.ngleanhvu.dsa_training_system.security.JwtUtil;
import com.ngleanhvu.dsa_training_system.service.CommentService;
import com.ngleanhvu.dsa_training_system.util.AppUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;
    private final JwtUtil jwtUtil;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/discuss/{discussId}")
    public ResponseEntity<?> createComment(@RequestBody CommentRequest request,
                                           @PathVariable("discussId") Integer discussId,
                                           @RequestHeader("Authorization") String token) {

        String userId = jwtUtil.getUserIdFromToken(token);
        request.setUserId(userId);
        commentService.createComment(request, discussId);
        var response = ApiResponse.builder()
                .message("Comment create success")
                .status(HttpStatus.CREATED.name())
                .metadata(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/problems/{problemId}")
    public ResponseEntity<?> createCommentForProblem(@Valid @RequestBody CommentRequest request,
                                           @PathVariable("problemId") Integer problemId,
                                           @RequestHeader("Authorization") String token) {
        String userId = jwtUtil.getUserIdFromToken(token);
        log.info("userId = {}", userId);
        request.setUserId(userId);
        commentService.createCommentForProblem(request, problemId);
        var response = ApiResponse.builder()
                .message("Comment create success")
                .status(HttpStatus.CREATED.name())
                .metadata(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/parent/{parentCommentId}/credentials")
    public ResponseEntity<?> getCommentsByParentCommentWithUser(@PathVariable("parentCommentId") Integer parentCommentId,
                                                                @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
                                                                @RequestParam(required = false, defaultValue = "1") int page,
                                                                @RequestParam(required = false, defaultValue = "5") int size,
                                                                @RequestParam(required = false, defaultValue = "desc") String sortDir,
                                                                @RequestHeader("Authorization") String token) {
        String userId = jwtUtil.getUserIdFromToken(token);
        PagingSearch pagingSearch = AppUtil.toPagingSearch(AppUtil.changeSortBy(sortBy), sortDir, Math.max(page-1,0), size);
        var response = commentService.getCommentsByParentCommentWithUser(parentCommentId, userId, pagingSearch);
        var apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.name())
                .metadata(response)
                .message("Comment get success")
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/parent/{parentCommentId}")
    public ResponseEntity<?> getParentComment(@PathVariable("parentCommentId") Integer parentCommentId,
                                              @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
                                              @RequestParam(required = false, defaultValue = "1") int page,
                                              @RequestParam(required = false, defaultValue = "5") int size,
                                              @RequestParam(required = false, defaultValue = "desc") String sortDir) {
        PagingSearch pagingSearch = AppUtil.toPagingSearch(sortBy, sortDir, Math.max(page-1,0), size);
        var response = commentService.getChildCommentsByParentComment(parentCommentId, pagingSearch);
        var apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.name())
                .metadata(response)
                .message("Comment get success")
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/problems/{problemId}")
    public ResponseEntity<?> getProblemComment(@PathVariable("problemId") Integer problemId,
                                               @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
                                               @RequestParam(required = false, defaultValue = "1") int page,
                                               @RequestParam(required = false, defaultValue = "5") int size,
                                               @RequestParam(required = false, defaultValue = "desc") String sortDir) {
        PagingSearch pagingSearch = AppUtil.toPagingSearch(sortBy, sortDir, Math.max(page-1,0), size);
        var response = commentService.getCommentsByProblem(problemId, pagingSearch, null);
        var apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.name())
                .metadata(response)
                .message("Comment get success")
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/problems/{problemId}/credentials")
    public ResponseEntity<?> getProblemCommentWithCredentials(@PathVariable("problemId") Integer problemId,
                                               @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
                                               @RequestParam(required = false, defaultValue = "1") int page,
                                               @RequestParam(required = false, defaultValue = "5") int size,
                                               @RequestParam(required = false, defaultValue = "desc") String sortDir,
                                               @RequestHeader("Authorization") String token) {
        String userId = jwtUtil.getUserIdFromToken(token);
        PagingSearch pagingSearch = AppUtil.toPagingSearch(AppUtil.changeSortBy(sortBy), sortDir, Math.max(page-1,0), size);
        var response = commentService.getCommentsByProblem(problemId, pagingSearch, userId);
        var apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.name())
                .metadata(response)
                .message("Comment get success")
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


    @GetMapping("/discuss/{discussId}")
    public ResponseEntity<?> getDiscuss(@PathVariable("discussId") Integer discussId,
                                        @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
                                        @RequestParam(required = false, defaultValue = "1") int page,
                                        @RequestParam(required = false, defaultValue = "5") int size,
                                        @RequestParam(required = false, defaultValue = "desc") String sortDir) {
        PagingSearch pagingSearch = AppUtil.toPagingSearch(sortBy, sortDir, Math.max(page-1,0), size);
        var response = commentService.getCommentsByDiscuss(discussId, pagingSearch);
        var apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.name())
                .metadata(response)
                .message("Comment get success")
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/discuss/credential/{discussId}")
    public ResponseEntity<?> getDiscussWithCredential(@PathVariable("discussId") Integer discussId,
                                        @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
                                        @RequestParam(required = false, defaultValue = "1") int page,
                                        @RequestParam(required = false, defaultValue = "5") int size,
                                        @RequestParam(required = false, defaultValue = "desc") String sortDir,
                                        @RequestHeader("Authorization") String token) {
        String userId = jwtUtil.getUserIdFromToken(token);
        PagingSearch pagingSearch = AppUtil.toPagingSearch(AppUtil.changeSortBy(sortBy), sortDir, Math.max(page-1,0), size);
        var response = commentService.getCommentsByDiscussWithCredential(discussId, userId, pagingSearch);
        var apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.name())
                .metadata(response)
                .message("Comment get success")
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PatchMapping("/{commentId}")
    public ResponseEntity<?> updateComment(@Valid @RequestBody CommentUpdateRequest request,
                                           @PathVariable("commentId") Integer commentId,
                                           @RequestHeader("Authorization") String token) {

        String userId = jwtUtil.getUserIdFromToken(token);
        request.setUserId(userId);
        commentService.updateComment(commentId, request);
        var response = ApiResponse.builder()
                .status(HttpStatus.OK.name())
                .message("Comment update success")
                .metadata(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable("commentId") Integer commentId,
                                           @RequestHeader("Authorization") String token) {

        String userId = jwtUtil.getUserIdFromToken(token);
        commentService.deleteComment(commentId, userId);
        var apiResponse = ApiResponse.builder()
                .status(HttpStatus.NO_CONTENT.name())
                .message("Comment delete success")
                .metadata(null)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/{commentId}/toggle")
    public ResponseEntity<?> toggleVote(@RequestHeader("Authorization") String token,
                                        @PathVariable("commentId") Integer commentId) {
        String userId = jwtUtil.getUserIdFromToken(token);
        commentService.toggleVote(userId, commentId);
        var apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.name())
                .message("Comment toggle success")
                .metadata(null)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

}
