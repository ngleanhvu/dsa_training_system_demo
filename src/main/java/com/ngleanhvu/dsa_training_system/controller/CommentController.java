package com.ngleanhvu.dsa_training_system.controller;

import com.ngleanhvu.dsa_training_system.dto.request.CommentRequest;
import com.ngleanhvu.dsa_training_system.dto.response.ApiResponse;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import com.ngleanhvu.dsa_training_system.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/discuss/{discussId}")
    public ResponseEntity<?> createComment(@RequestBody CommentRequest request,
                                           @PathVariable("discussId") Integer discussId) {
        commentService.createComment(request, discussId);
        var response = ApiResponse.builder()
                .message("Comment create success")
                .status(HttpStatus.CREATED.name())
                .metadata(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/parent/{parentCommentId}")
    public ResponseEntity<?> getParentComment(@PathVariable("parentCommentId") Integer parentCommentId,
                                              @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
                                              @RequestParam(required = false, defaultValue = "1") int page,
                                              @RequestParam(required = false, defaultValue = "5") int size,
                                              @RequestParam(required = false, defaultValue = "desc") String sortDir) {
        PagingSearch pagingSearch = new PagingSearch();
        pagingSearch.setSortBy(sortBy);
        pagingSearch.setPage(page);
        pagingSearch.setSize(size);
        pagingSearch.setDirection(sortDir);

        var response = commentService.getChildCommentsByParentComment(parentCommentId, pagingSearch);
        log.info("response: {}", response);

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
        PagingSearch pagingSearch = new PagingSearch();
        pagingSearch.setSortBy(sortBy);
        pagingSearch.setPage(page);
        pagingSearch.setSize(size);
        pagingSearch.setDirection(sortDir);

        var response = commentService.getCommentsByDiscuss(discussId, pagingSearch);
        log.info("response: {}", response);

        var apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.name())
                .metadata(response)
                .message("Comment get success")
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
