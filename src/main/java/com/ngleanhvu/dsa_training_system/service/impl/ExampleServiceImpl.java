package com.ngleanhvu.dsa_training_system.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ngleanhvu.dsa_training_system.dto.request.ExampleCreateRequest;
import com.ngleanhvu.dsa_training_system.dto.request.ExampleUpdateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.*;
import com.ngleanhvu.dsa_training_system.entity.Example;
import com.ngleanhvu.dsa_training_system.entity.Problem;
import com.ngleanhvu.dsa_training_system.exception.ResourceNotFoundException;
import com.ngleanhvu.dsa_training_system.mappter.ExampleMapper;
import com.ngleanhvu.dsa_training_system.repo.ExampleRepo;
import com.ngleanhvu.dsa_training_system.repo.ProblemRepo;
import com.ngleanhvu.dsa_training_system.service.ExampleService;
import com.ngleanhvu.dsa_training_system.service.S3Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @Override
    public Example createExample(ExampleCreateRequest request, Problem problem) throws JsonProcessingException {

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
        log.info("Upload images: {}", imagesJson);

        return Example.builder()
                .input(request.getInput())
                .output(request.getOutput())
                .problem(problem)
                .status(1)
                .explantation(request.getExplanation())
                .images(imagesJson)
                .build();

    }

    @Transactional
    @Override
    public void createExample(ExampleCreateRequest requests, int problemId) throws JsonProcessingException {

        Problem problem = problemRepo.findById(problemId)
                .orElseThrow(() -> new ResourceNotFoundException("Problem", "id", String.valueOf(problemId)));


        Example example = createExample(requests, problem);
        log.info("Created example: {}", example.toString());
        exampleRepo.save(example);
    }

    @Override
    public ListExampleResponse getExamples(Integer problemId,
                                           PagingSearch pagingSearch) {
        log.info("problemId: {}", problemId);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Example> query = cb.createQuery(Example.class);
        Root<Example> root = query.from(Example.class);

        List<Predicate> predicates = new ArrayList<>();

        if (problemId != 0) {
            log.info("problemId: {}", problemId);
            predicates.add(cb.equal(root.get("problem").get("problemId"), problemId));
        }

        query.select(root)
                .where(cb.and(predicates.toArray(new Predicate[0])))
                .orderBy(cb.asc(root.get("exampleId")));

        TypedQuery<Example> typedQuery = entityManager.createQuery(query);

        int page = pagingSearch.getPage();
        int size = pagingSearch.getSize();

        typedQuery.setFirstResult(page * size);
        typedQuery.setMaxResults(size);

        List<Example> examples= typedQuery.getResultList();

        List<ExampleResponse> exampleResponses  = examples.stream()
                .map(ExampleMapper::mapToDto)
                .toList();

        log.info("size: {}", examples.size());

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Example> countRoot = countQuery.from(Example.class);

        List<Predicate> countPredicates = new ArrayList<>();

        if (problemId != 0) {
            countPredicates.add(cb.equal(countRoot.get("problem").get("problemId"), problemId));
        }

        countQuery.select(cb.count(countRoot))
                .where(cb.and(countPredicates.toArray(new Predicate[0])));

        Long total = entityManager.createQuery(countQuery).getSingleResult();

        int totalPages = (int) Math.ceil((double) total / size);

        log.info("totalPages: {}", totalPages);
        log.info("total: {}", total);
        log.info("size: {}", size);

        return ListExampleResponse.builder()
                .totalPages(totalPages)
                .page(page)
                .examples(exampleResponses)
                .build();
    }

    @Transactional
    @Override
    public void updateExample(Integer exampleId, ExampleUpdateRequest request) {
        Example example = exampleRepo.findById(exampleId)
                .orElseThrow(() -> new ResourceNotFoundException("Example", "id", String.valueOf(exampleId)));
        log.info("Updating example: {}", example.toString());
        example.setInput(request.getInput());
        example.setOutput(request.getOutput());
        example.setExplantation(request.getExplanation());

        List<MultipartFile> files = request.getFiles();

        if (files != null && !files.isEmpty()) {
            try {
                List<String> newUrls = files.stream()
                        .map(file -> {
                            try {
                                return s3Service.upload(file);
                            } catch (IOException e) {
                                log.error("Upload error: {}", e.getMessage());
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .toList();

                String imagesJson = objectMapper.writeValueAsString(newUrls);
                example.setImages(imagesJson);
            } catch (JsonProcessingException e) {
                log.error("JSON error: {}", e.getMessage());
            }
        }

        exampleRepo.save(example);
    }


    @Transactional
    @Override
    public void deleteExample(Integer exampleId) {
        exampleRepo.deleteById(exampleId);
    }

    @Override
    public ExampleResponse getExampleById(Integer exampleId) {
        Example example = exampleRepo.findById(exampleId)
                .orElseThrow(() -> new ResourceNotFoundException("Example", "id", String.valueOf(exampleId)));
        log.info("example: {}", example.toString());
        ExampleResponse exampleResponse = ExampleMapper.mapToDto(example);
        log.info("exampleResponse: {}", exampleResponse.toString());
        return exampleResponse;
    }


}
