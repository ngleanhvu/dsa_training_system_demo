package com.ngleanhvu.dsa_training_system.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ngleanhvu.dsa_training_system.dto.response.DifficultResponse;

import java.util.List;

public interface DifficultService {
    List<DifficultResponse> getDifficulties() throws JsonProcessingException;
}
