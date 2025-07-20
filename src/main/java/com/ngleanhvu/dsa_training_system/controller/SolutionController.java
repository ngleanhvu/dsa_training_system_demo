package com.ngleanhvu.dsa_training_system.controller;

import com.ngleanhvu.dsa_training_system.dto.request.DiscussCreateRequest;
import com.ngleanhvu.dsa_training_system.dto.request.DiscussFilterRequest;
import com.ngleanhvu.dsa_training_system.dto.response.ApiResponse;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import com.ngleanhvu.dsa_training_system.service.SolutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/solutions")
@RequiredArgsConstructor
@Slf4j
public class SolutionController {

    private final SolutionService solutionService;

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/problems/{problemId}")
    public ResponseEntity<?> createSolution(@RequestBody DiscussCreateRequest discussCreateRequest,
                                            @PathVariable("problemId") Integer problemId) {
        solutionService.createSolution(problemId, discussCreateRequest);
        var apiResponse = ApiResponse.builder()
                .message("Solution created")
                .status(HttpStatus.CREATED.name())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @PostMapping
    public ResponseEntity<?> getSolutions(@RequestBody DiscussFilterRequest discussFilterRequest,
                                          @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
                                          @RequestParam(required = false, defaultValue = "1") int page,
                                          @RequestParam(required = false, defaultValue = "10") int size,
                                          @RequestParam(required = false, defaultValue = "desc") String sortDir) {
        PagingSearch pagingSearch = new PagingSearch();
        pagingSearch.setPage(page);
        pagingSearch.setSize(size);
        pagingSearch.setSortBy(sortBy);
        pagingSearch.setDirection(sortDir);
        var response = solutionService.getSolutions(discussFilterRequest, pagingSearch);
        var apiResponse = ApiResponse.builder()
                .message("Get solutions success")
                .status(HttpStatus.OK.name())
                .metadata(response)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


}
