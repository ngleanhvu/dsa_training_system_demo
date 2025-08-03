package com.ngleanhvu.dsa_training_system.service;

import com.ngleanhvu.dsa_training_system.dto.response.ProgrammingLanguageResponse;

import java.util.List;

public interface ProgrammingLanguageService {
    List<ProgrammingLanguageResponse> getProgrammingLanguages();
}
