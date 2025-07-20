package com.ngleanhvu.dsa_training_system.controller;

import com.ngleanhvu.dsa_training_system.dto.request.ContestCreateRequest;
import com.ngleanhvu.dsa_training_system.dto.request.ContestFilterRequest;
import com.ngleanhvu.dsa_training_system.dto.request.ContestUpdateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.ApiResponse;
import com.ngleanhvu.dsa_training_system.dto.response.ContestDetailResponse;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import com.ngleanhvu.dsa_training_system.service.ContestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/contests")
public class ContestController {
    private final ContestService contestService;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createContest(@RequestBody ContestCreateRequest request) {
        contestService.createContest(request);
        var apiResponse = ApiResponse.builder()
                .message("Contest created")
                .status(HttpStatus.CREATED.name())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<?> getContests(@RequestBody ContestFilterRequest request,
                                         @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
                                         @RequestParam(required = false, defaultValue = "1") int page,
                                         @RequestParam(required = false, defaultValue = "5") int size,
                                         @RequestParam(required = false, defaultValue = "desc") String sortDir) {

        PagingSearch pagingSearch = new PagingSearch();
        pagingSearch.setSortBy(sortBy);
        pagingSearch.setPage(page);
        pagingSearch.setSize(size);
        pagingSearch.setDirection(sortDir);

        var contestResponses = contestService.getContests(request, pagingSearch);

        var apiResponse = ApiResponse.builder()
                .message("Get contests success")
                .status(HttpStatus.OK.name())
                .metadata(contestResponses)
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/{contestId}")
    public ResponseEntity<?> getContestDetail(@PathVariable("contestId") Integer contestId) {
        ContestDetailResponse contestDetailResponse = contestService.getContestDetail(contestId);
        var apiResponse = ApiResponse.builder()
                .message("Get contest detail success")
                .status(HttpStatus.OK.name())
                .metadata(contestDetailResponse)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{contestId}")
    public ResponseEntity<?> deleteContest(@PathVariable("contestId") Integer contestId) {
        contestService.deleteContest(contestId);
        var apiResponse = ApiResponse.builder()
                .message("Delete contest success")
                .status(HttpStatus.NO_CONTENT.name())
                .metadata(null)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/{contestId}")
    public ResponseEntity<?> updateContest(@PathVariable("contestId") Integer contestId,
                                           @RequestBody ContestUpdateRequest request) {
        contestService.updateContest(contestId, request);
        var apiResponse = ApiResponse.builder()
                .message("Update contest success")
                .status(HttpStatus.OK.name())
                .metadata(null)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
