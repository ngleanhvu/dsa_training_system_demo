package com.ngleanhvu.dsa_training_system.controller;

import com.ngleanhvu.dsa_training_system.dto.response.ApiResponse;
import com.ngleanhvu.dsa_training_system.service.StatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/stats/users")
@RequiredArgsConstructor
public class StatisticUserController {

    private final StatisticService statisticService;

    @GetMapping("/difficult/{email}")
    public ResponseEntity<?> getDifficultSolvedUser(@PathVariable("email") String email) {
        var response = statisticService.getDifficultUserResponse(email);
        var apiResponse = ApiResponse.builder()
                .metadata(response)
                .status(HttpStatus.OK.name())
                .message("Get difficult user response")
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/total-solved/{email}")
    public ResponseEntity<?> getTotalSolvedUser(@PathVariable("email") String email) {
        var response = statisticService.getProblemUserSolved(email);
        var apiResponse = ApiResponse.builder()
                .metadata(response)
                .status(HttpStatus.OK.name())
                .message("Get total solved user response")
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/submissions/{email}/year/{year}")
    public ResponseEntity<?> getSubmissionsUser(@PathVariable("email") String email,
                                                @PathVariable("year") Integer year) {
        var response = statisticService.getSubmissionCountResponse(email, year);
        var apiResponse = ApiResponse.builder()
                .metadata(response)
                .status(HttpStatus.OK.name())
                .message("Get submissions user response")
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
