package com.ngleanhvu.dsa_training_system.controller;

import com.ngleanhvu.dsa_training_system.dto.response.ApiResponse;
import com.ngleanhvu.dsa_training_system.security.JwtUtil;
import com.ngleanhvu.dsa_training_system.service.ContestParticipantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/contest-participants")
@Slf4j
public class ContestParticipantController {

    private final ContestParticipantService contestParticipantService;
    private final JwtUtil jwtUtil;

    @PostMapping("/contests/{contestId}/enroll")
    public ResponseEntity<?> enroll(@PathVariable Integer contestId,
                                    @RequestHeader("Authorization") String authHeader) {
        String userId = jwtUtil.getUserIdFromToken(authHeader);
        log.info("Enroll contest participant with id {}", contestId);
        contestParticipantService.enroll(userId, contestId);
        var apiResponse = ApiResponse.builder()
                .message("Successfully enrolled contest participant with id " + contestId)
                .status(HttpStatus.CREATED.name());
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @GetMapping("/contests/{contestId}")
    public ResponseEntity<?> getContestParticipant(@PathVariable Integer contestId) {
        log.info("Get contest participant with id {}", contestId);
        var response = contestParticipantService.getContestParticipantLeaderboard(contestId);
        var apiResponse = ApiResponse.builder()
                .message("Successfully retrieved contest participant with id " + contestId)
                .status(HttpStatus.OK.name())
                .metadata(response)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

}
