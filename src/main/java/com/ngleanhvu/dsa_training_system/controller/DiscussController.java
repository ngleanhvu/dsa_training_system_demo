package com.ngleanhvu.dsa_training_system.controller;

import com.ngleanhvu.dsa_training_system.dto.request.DiscussCreateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.ApiResponse;
import com.ngleanhvu.dsa_training_system.service.DiscussService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/discuss")
public class DiscussController {

    private final DiscussService discussService;

    @PostMapping
    public ResponseEntity<?> createDiscuss(@RequestBody DiscussCreateRequest discussCreateRequest) {
        discussService.createDiscuss(discussCreateRequest);
        var response = ApiResponse.builder()
                .message("Discuss create success")
                .metadata(null)
                .status(HttpStatus.CREATED.name())
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
