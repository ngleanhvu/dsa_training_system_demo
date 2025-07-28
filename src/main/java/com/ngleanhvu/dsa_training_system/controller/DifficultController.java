package com.ngleanhvu.dsa_training_system.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ngleanhvu.dsa_training_system.dto.response.ApiResponse;
import com.ngleanhvu.dsa_training_system.service.DifficultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/difficulties")
public class DifficultController {
    private final DifficultService difficultService;

    @GetMapping
    public ResponseEntity<?> getDifficulties() throws JsonProcessingException {
        var responses = difficultService.getDifficulties();
        var apiResponses = ApiResponse.builder()
                .message("Get difficulties success")
                .status(HttpStatus.OK.name())
                .metadata(responses)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponses);
    }
}
