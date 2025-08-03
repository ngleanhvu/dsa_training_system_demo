package com.ngleanhvu.dsa_training_system.service.impl;

import com.ngleanhvu.dsa_training_system.dto.response.ProgrammingLanguageResponse;
import com.ngleanhvu.dsa_training_system.repo.ProgrammingLanguageRepo;
import com.ngleanhvu.dsa_training_system.service.ProgrammingLanguageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProgrammingLanguageServiceImpl implements ProgrammingLanguageService {
    private final ProgrammingLanguageRepo programmingLanguageRepo;
    @Override
    public List<ProgrammingLanguageResponse> getProgrammingLanguages() {
        return programmingLanguageRepo.findAll().stream()
                .map(p -> ProgrammingLanguageResponse.builder()
                        .programmingLanguageId(p.getProgrammingLanguageId())
                        .programmingLanguageName(p.getName())
                        .build())
                .toList();
    }
}
