package com.ngleanhvu.dsa_training_system.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ngleanhvu.dsa_training_system.dto.request.ExampleCreateRequest;
import com.ngleanhvu.dsa_training_system.dto.request.ExampleUpdateInfoRequest;
import com.ngleanhvu.dsa_training_system.dto.request.ExampleUpdateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.ApiResponse;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import com.ngleanhvu.dsa_training_system.service.ExampleService;
import jakarta.validation.Valid;
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
                                         @Valid @ModelAttribute ExampleCreateRequest requests) throws JsonProcessingException {
        exampleService.createExample(requests, problemId);
        var response = ApiResponse.builder()
                .message("Create success examples for problem with id " + problemId)
                .status(HttpStatus.CREATED.name())
                .metadata(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<?> getExample(@RequestParam(required = false, defaultValue = "0") Integer problemId,
                                        @RequestParam(required = false, defaultValue = "1") Integer page,
                                        @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                        @RequestParam(required = false, defaultValue = "asc") String sortDirection,
                                        @RequestParam(required = false, defaultValue = "exampleId") String sortBy) {
        PagingSearch pagingSearch = new PagingSearch();
        pagingSearch.setPage(Math.max(page-1, 0));
        pagingSearch.setDirection(sortDirection);
        pagingSearch.setSortBy(sortBy);
        pagingSearch.setSize(pageSize);

        var response = exampleService.getExamples(problemId, pagingSearch);
        var api = ApiResponse.builder()
                .message("Get examples success")
                .metadata(response)
                .status(HttpStatus.OK.name())
                .build();
        return new ResponseEntity<>(api, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/{exampleId}")
    public ResponseEntity<?> updateExample(@PathVariable("exampleId") Integer exampleId,
                                           @Valid @ModelAttribute ExampleUpdateRequest request) {
        exampleService.updateExample(exampleId, request);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(HttpStatus.OK.name())
                        .message("Example info updated")
                        .build()
        );
    }

    @GetMapping("/{exampleId}")
    public ResponseEntity<?> getExample(@PathVariable("exampleId") Integer exampleId) {
        var response = exampleService.getExampleById(exampleId);
        var api = ApiResponse.builder()
                .message("Get example success")
                .status(HttpStatus.OK.name())
                .metadata(response)
                .build();
        return new ResponseEntity<>(api, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{exampleId}")
    public ResponseEntity<?> deleteExample(@PathVariable("exampleId") Integer exampleId) {
        exampleService.deleteExample(exampleId);
        var response = ApiResponse.builder()
                .status(HttpStatus.NO_CONTENT.name())
                .message("Example deleted")
                .build();
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }
}
