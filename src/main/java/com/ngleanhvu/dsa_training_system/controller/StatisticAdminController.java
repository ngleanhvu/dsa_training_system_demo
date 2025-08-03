package com.ngleanhvu.dsa_training_system.controller;

import com.ngleanhvu.dsa_training_system.dto.request.YearRequest;
import com.ngleanhvu.dsa_training_system.dto.response.ApiResponse;
import com.ngleanhvu.dsa_training_system.service.StatisticService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/stats/admin")
@RequiredArgsConstructor
@Slf4j
public class StatisticAdminController {
    private final StatisticService statisticService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/common")
    public ResponseEntity<?> getCommonUserStatsEachYear() {
        var response = statisticService.getCommonStatisticForAdmin();
        var apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.name())
                .metadata(response)
                .message("Get Common Stats")
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/top5-submissions")
    public ResponseEntity<?> getTop5Submissions() {
        var response = statisticService.getTop5ProblemSubmission();
        var apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.name())
                .metadata(response)
                .message("Get Top 5 Submissions")
                .build();
        log.info("getTop5ProblemSubmission: {}", response);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/difficult")
    public ResponseEntity<?> getDifficultStatsEachYear() {
        var response = statisticService.getDifficultStatsResponse();
        var apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.name())
                .metadata(response)
                .message("Get Difficult Stats")
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/submissions")
    public ResponseEntity<?> getSubmissionStatsEachYear(@RequestBody YearRequest submissionYearRequest) {
        var response = statisticService.getSubmissionStatsEachYear(submissionYearRequest.getYear());
        var apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.name())
                .metadata(response)
                .message("Get Submission Stats")
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/users")
    public ResponseEntity<?> getUserStatsEachYear(@RequestBody YearRequest userYearRequest) {
        var response = statisticService.getUserStatsEachYear(userYearRequest.getYear());
        var apiResponse = ApiResponse.builder()
                .status(HttpStatus.OK.name())
                .metadata(response)
                .message("Get User Stats")
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
