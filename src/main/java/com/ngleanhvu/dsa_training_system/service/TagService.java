package com.ngleanhvu.dsa_training_system.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ngleanhvu.dsa_training_system.dto.response.TagResponse;

import java.util.List;

public interface TagService {
    List<TagResponse> getTags() throws JsonProcessingException;
}
