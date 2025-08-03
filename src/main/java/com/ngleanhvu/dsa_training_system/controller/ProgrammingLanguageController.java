package com.ngleanhvu.dsa_training_system.controller;

import com.ngleanhvu.dsa_training_system.dto.response.ApiResponse;
import com.ngleanhvu.dsa_training_system.service.ProgrammingLanguageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/programming-languages")
@RequiredArgsConstructor
public class ProgrammingLanguageController {
    private final ProgrammingLanguageService programmingLanguageService;

    @GetMapping
    public ResponseEntity<?> getAllProgrammingLanguages() {
        var response = programmingLanguageService.getProgrammingLanguages();
        var apiResponse = ApiResponse.builder()
                .message("Get all programming languages")
                .metadata(response)
                .status(HttpStatus.OK.name())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
