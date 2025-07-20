package com.ngleanhvu.dsa_training_system.mappter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ngleanhvu.dsa_training_system.dto.response.ExampleResponse;
import com.ngleanhvu.dsa_training_system.entity.Example;

import java.util.ArrayList;
import java.util.List;

public class ExampleMapper {
    public static ExampleResponse mapToDto(Example example) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> imagesList = new ArrayList<>();

        try {
            if (example.getImages() != null) {
                imagesList = objectMapper.readValue(example.getImages(), new TypeReference<>() {
                });
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return ExampleResponse.builder()
                .input(example.getInput())
                .output(example.getOutput())
                .explanation(example.getExplantation())
                .images(imagesList)
                .build();
    }
}
