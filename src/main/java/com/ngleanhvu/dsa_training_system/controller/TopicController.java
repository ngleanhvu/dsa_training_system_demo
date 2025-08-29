package com.ngleanhvu.dsa_training_system.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ngleanhvu.dsa_training_system.dto.request.TopicCreateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.ApiResponse;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import com.ngleanhvu.dsa_training_system.service.TopicService;
import com.ngleanhvu.dsa_training_system.util.AppUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api/v1/topics")
public class TopicController {
    private final TopicService topicService;

    @GetMapping
    public ResponseEntity<?> getTopics() throws JsonProcessingException {
        var response = topicService.getTopics();
        var apiResponse = ApiResponse.builder()
                .message("Get topics success")
                .status(HttpStatus.OK.name())
                .metadata(response)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/admin")
    public ResponseEntity<?> getAllTopics(@RequestParam(value = "keyword", defaultValue = "") String keyword,
                                          @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
                                          @RequestParam(required = false, defaultValue = "1") int page,
                                          @RequestParam(required = false, defaultValue = "10") int size,
                                          @RequestParam(required = false, defaultValue = "desc") String sortDir) {
        PagingSearch pagingSearch = AppUtil.toPagingSearch(sortBy, sortDir, Math.max(page-1, 0) , size);
        var response = topicService.getAllTopics(keyword, pagingSearch);
        var apiResponse = ApiResponse.builder()
                .message("Get topics success")
                .status(HttpStatus.OK.name())
                .metadata(response)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getTopicStats() {
        var response = topicService.statsTopic();
        var apiResponse = ApiResponse.builder()
                .message("Get topic stats success")
                .status(HttpStatus.OK.name())
                .metadata(response)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createTopic(@RequestBody TopicCreateRequest topicRequest)  {
        topicService.createTopic(topicRequest);
        var response = ApiResponse.builder()
                .message("Create topic")
                .status(HttpStatus.CREATED.name())
                .metadata(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{topicId}")
    public ResponseEntity<?> updateTopic(@PathVariable("topicId") Integer topicId,
                                         @RequestBody TopicCreateRequest topicRequest) {
        topicService.updateTopic(topicId, topicRequest);
        var response = ApiResponse.builder()
                .message("Update topic")
                .status(HttpStatus.OK.name())
                .metadata(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{topicId}")
    public ResponseEntity<?> deleteTopic(@PathVariable("topicId") Integer topicId) {
        topicService.deleteTopic(topicId);
        var response = ApiResponse.builder()
                .message("Delete topic")
                .status(HttpStatus.NO_CONTENT.name())
                .metadata(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
