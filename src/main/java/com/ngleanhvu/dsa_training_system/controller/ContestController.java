package com.ngleanhvu.dsa_training_system.controller;

import com.ngleanhvu.dsa_training_system.dto.request.ContestCreateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.ApiResponse;
import com.ngleanhvu.dsa_training_system.service.ContestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/contests")
public class ContestController {
    private final ContestService contestService;

    @PostMapping
    public ResponseEntity<?> createContest(@RequestBody ContestCreateRequest request) {
        contestService.createContest(request);
        var apiResponse = ApiResponse.builder()
                .message("Contest created")
                .status(HttpStatus.CREATED.name())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }
}
