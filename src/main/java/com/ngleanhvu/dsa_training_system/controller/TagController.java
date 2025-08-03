package com.ngleanhvu.dsa_training_system.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ngleanhvu.dsa_training_system.dto.response.ApiResponse;
import com.ngleanhvu.dsa_training_system.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;

    @GetMapping
    public ResponseEntity<?> getTags() throws JsonProcessingException {
        var tags = tagService.getTags();

        var apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.name())
                .metadata(tags)
                .message("Get all tags")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
