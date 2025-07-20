package com.ngleanhvu.dsa_training_system.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ngleanhvu.dsa_training_system.dto.request.ExampleCreateRequest;
import com.ngleanhvu.dsa_training_system.dto.request.ExampleUpdateInfoRequest;
import com.ngleanhvu.dsa_training_system.dto.response.ApiResponse;
import com.ngleanhvu.dsa_training_system.service.ExampleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/examples")
public class ExampleController {

    private final ExampleService exampleService;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping(value = "/problems/{problemId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> postExample(@PathVariable("problemId") int problemId,
                                         @ModelAttribute List<ExampleCreateRequest> requests) throws JsonProcessingException {
        exampleService.createExamples(requests, problemId);
        var response = ApiResponse.builder()
                .message("Create success examples for problem with id " + problemId)
                .status(HttpStatus.CREATED.name())
                .metadata(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/problem/{problemId}")
    public ResponseEntity<?> getExample(@PathVariable("problemId") int problemId) {
        var response = exampleService.getExamples(problemId);
        var api = ApiResponse.builder()
                .message("Get examples success")
                .metadata(response)
                .status(HttpStatus.OK.name())
                .build();
        return new ResponseEntity<>(api, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/{exampleId}")
    public ResponseEntity<?> updateExampleInfo(@PathVariable("exampleId") Integer exampleId,
                                               @RequestBody ExampleUpdateInfoRequest request) {
        exampleService.updateExampleInfo(exampleId, request);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(HttpStatus.OK.name())
                        .message("Example info updated")
                        .build()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PatchMapping("/{id}/images")
    public ResponseEntity<?> updateExampleImages(@PathVariable("id") Integer exampleId,
                                                 @RequestParam("files") List<MultipartFile> files) {
        exampleService.updateExampleImages(exampleId, files);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(HttpStatus.OK.name())
                        .message("Example images updated")
                        .build()
        );
    }


}
