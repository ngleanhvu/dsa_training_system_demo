package com.ngleanhvu.dsa_training_system.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ngleanhvu.dsa_training_system.dto.request.ExampleCreateRequest;
import com.ngleanhvu.dsa_training_system.entity.Example;
import com.ngleanhvu.dsa_training_system.entity.Problem;
import com.ngleanhvu.dsa_training_system.exception.ResourceNotFoundException;
import com.ngleanhvu.dsa_training_system.repo.ExampleRepo;
import com.ngleanhvu.dsa_training_system.repo.ProblemRepo;
import com.ngleanhvu.dsa_training_system.service.ExampleService;
import com.ngleanhvu.dsa_training_system.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExampleServiceImpl implements ExampleService {

    private final ExampleRepo exampleRepo;
    private final ProblemRepo problemRepo;
    private final S3Service s3Service;
    private final ObjectMapper objectMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public Example createExample(ExampleCreateRequest request, int problemId) throws JsonProcessingException {
        Problem problem = problemRepo.findById(problemId)
                .orElseThrow(() -> new ResourceNotFoundException("Problem", "id", String.valueOf(problemId)));

        List<MultipartFile> files = request.getFiles();

        List<CompletableFuture<String>> futures = files.stream()
                .map(file -> CompletableFuture.supplyAsync(() -> {
                    try {
                        return s3Service.upload(file);
                    } catch (IOException e) {
                        log.error("Error uploading file: {}", e.getMessage());
                        return null;
                    }
                })
                )
                .toList();

        List<String> imageUrls = futures.stream()
                .map(future -> {
                    try {
                        return future.get();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        log.error("Upload interrupted: {}", e.getMessage());
                        return null;
                    } catch (ExecutionException e) {
                        log.error("Upload failed: {}", e.getCause().getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();

        String imagesJson = imageUrls.isEmpty() ? "" : objectMapper.writeValueAsString(imageUrls);

        return Example.builder()
                .input(request.getInput())
                .output(request.getOutput())
                .problem(problem)
                .explantation(request.getExplanation())
                .images(imagesJson)
                .build();

    }

    @Transactional
    @Override
    public void createExamples(List<ExampleCreateRequest> requests, int problemId) {

        List<Example> examples = new ArrayList<>();

        List<CompletableFuture<Void>> tasks = requests.stream()
                .map(request -> CompletableFuture.runAsync(() -> {
                    try {
                        Example e = this.createExample(request, problemId);
                        examples.add(e);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Failed to create example", e);
                    }
                }))
                .toList();

        for (CompletableFuture<Void> task : tasks) {
            try {
                task.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread interrupted", e);
            } catch (ExecutionException e) {
                throw new RuntimeException("Exception during example creation", e.getCause());
            }
        }

        exampleRepo.saveAll(examples);
    }
}
