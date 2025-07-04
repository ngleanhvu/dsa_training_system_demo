package com.ngleanhvu.dsa_training_system.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ngleanhvu.dsa_training_system.dto.request.ProblemCreateRequest;
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

    @PostMapping
    public ResponseEntity<?> createProblem(@Valid @RequestBody ProblemCreateRequest problemCreateRequest) throws JsonProcessingException {
        log.info("Creating problem: {}", problemCreateRequest);
        problemService.createProblem(problemCreateRequest);
        var response = ApiResponse.create("Create new problem success",
                HttpStatus.CREATED.name());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchProblem(@RequestBody ProblemSearchRequest problemSearchRequest,
                                           @RequestParam(required = false) String sortBy,
                                           @RequestParam(required = false) String sortDirection,
                                           @RequestParam(required = false) Integer page) {
        log.info("Searching problem: {}", problemSearchRequest);

        PagingSearch pagingSearch = new PagingSearch();
        pagingSearch.setSize(pageSize);
        pagingSearch.setPage(page);
        pagingSearch.setSortBy(sortBy);
        pagingSearch.setDirection(sortDirection);

        var response = problemSearchService.search(problemSearchRequest, new PagingSearch());
        var apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.name())
                .message("Search problem success")
                .metadata(response)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

}
