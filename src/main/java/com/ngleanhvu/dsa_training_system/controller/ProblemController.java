package com.ngleanhvu.dsa_training_system.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ngleanhvu.dsa_training_system.dto.request.ProblemCreateRequest;
import com.ngleanhvu.dsa_training_system.dto.request.ProblemSearchAdminRequest;
import com.ngleanhvu.dsa_training_system.dto.request.ProblemUpdateRequest;
import com.ngleanhvu.dsa_training_system.dto.request.SortRequest;
import com.ngleanhvu.dsa_training_system.dto.response.ApiResponse;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import com.ngleanhvu.dsa_training_system.elasticsearch.ProblemSearchRequest;
import com.ngleanhvu.dsa_training_system.elasticsearch.ProblemSearchService;
import com.ngleanhvu.dsa_training_system.service.ProblemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/problems")
@Slf4j
public class ProblemController {

    private final ProblemService problemService;
    private final ProblemSearchService problemSearchService;

    @Value("${page.size}")
    private int pageSize;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createProblem(@Valid @RequestBody ProblemCreateRequest problemCreateRequest) throws JsonProcessingException {
        log.info("Creating problem: {}", problemCreateRequest);
        problemService.createProblem(problemCreateRequest);
        var response = ApiResponse.create("Create new problem success",
                HttpStatus.CREATED.name());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/search")
    public ResponseEntity<?> searchProblem(@RequestBody ProblemSearchRequest problemSearchRequest,
                                           @RequestParam(required = false, defaultValue = "problemId") String sortBy,
                                           @RequestParam(required = false, defaultValue = "asc") String sortDirection,
                                           @RequestParam(required = false, defaultValue = "0") Integer page) {

        log.info("Searching problem: {}", problemSearchRequest);

        PagingSearch pagingSearch = new PagingSearch();

        pagingSearch.setSize(pageSize);
        pagingSearch.setPage(Math.max(page-1, 0));
        pagingSearch.setSortBy(sortBy);
        pagingSearch.setDirection(sortDirection);

        var response = problemSearchService.search(problemSearchRequest, pagingSearch);
        var apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.name())
                .message("Search problem success")
                .metadata(response)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/search/admin")
    public ResponseEntity<?> searchProblemAdmin(@RequestBody ProblemSearchAdminRequest problemSearchRequest,
                                                @RequestParam(required = false, defaultValue = "problemId") String sortBy,
                                                @RequestParam(required = false, defaultValue = "asc") String sortDirection,
                                                @RequestParam(required = false, defaultValue = "0") Integer page) {
        log.info("Searching problem admin: {}", problemSearchRequest);
        PagingSearch pagingSearch = new PagingSearch();

        pagingSearch.setSize(pageSize);
        pagingSearch.setPage(Math.max(page - 1, 0));
        pagingSearch.setSortBy(sortBy);
        pagingSearch.setDirection(sortDirection);

        log.info("Paging search: {}", pagingSearch);
        var response = problemService.getProblems(problemSearchRequest, pagingSearch);
        log.info("Search problem response: {}", response);
        var apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.name())
                .message("Search problem success")
                .metadata(response)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PatchMapping("/{problemId}/toggle-publish")
    public ResponseEntity<?> togglePublishProblem(@PathVariable("problemId") Integer problemId) throws JsonProcessingException {
        problemService.togglePublishProblem(problemId);
        var response = ApiResponse.builder()
                .status(HttpStatus.OK.name())
                .message("Problem published")
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/{problemId}")
    public ResponseEntity<?> updateProblem(@PathVariable("problemId") Integer problemId,
                                           @Valid @RequestBody ProblemUpdateRequest problemUpdateRequest) throws JsonProcessingException {
        problemService.updateProblem(problemId, problemUpdateRequest);
        var response = ApiResponse.builder()
                .status(HttpStatus.OK.name())
                .message("Problem updated")
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{problemId}")
    public ResponseEntity<?> getProblem(@PathVariable Integer problemId) throws JsonProcessingException {
        var response = problemService.getProblem(problemId);
        var apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.name())
                .message("Problem found")
                .metadata(response)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{problemId}")
    public ResponseEntity<?> deleteProblem(@PathVariable("problemId") Integer problemId) throws JsonProcessingException {
        problemService.deleteProblem(problemId);
        var response = ApiResponse.builder()
                .status(HttpStatus.NO_CONTENT.name())
                .message("Problem deleted")
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
