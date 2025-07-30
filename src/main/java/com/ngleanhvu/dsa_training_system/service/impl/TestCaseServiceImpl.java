package com.ngleanhvu.dsa_training_system.service.impl;

import com.ngleanhvu.dsa_training_system.dto.request.TestCaseCreateRequest;
import com.ngleanhvu.dsa_training_system.dto.request.TestCaseUpdateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.ListTestCaseResponse;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import com.ngleanhvu.dsa_training_system.dto.response.TestCaseResponse;
import com.ngleanhvu.dsa_training_system.entity.Problem;
import com.ngleanhvu.dsa_training_system.entity.TestCase;
import com.ngleanhvu.dsa_training_system.exception.ResourceNotFoundException;
import com.ngleanhvu.dsa_training_system.mappter.TestCaseMapper;
import com.ngleanhvu.dsa_training_system.repo.ProblemRepo;
import com.ngleanhvu.dsa_training_system.repo.TestCaseRepo;
import com.ngleanhvu.dsa_training_system.service.TestCaseService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestCaseServiceImpl implements TestCaseService {

    private final TestCaseRepo testCaseRepo;
    private final ProblemRepo problemRepo;

    @PersistenceContext
    private final EntityManager entityManager;

    @Transactional
    @Override
    public TestCase createTestCase(TestCaseCreateRequest request, int problemId) {

        Problem problem = problemRepo.findById(problemId)
                .orElseThrow(() -> new ResourceNotFoundException("Problem", "id", String.valueOf(problemId)));

        log.info("Test case: {}", request);

        return TestCase.builder()
                .input(request.getInput().trim())
                .output(request.getOutput().trim())
                .problem(problem)
                .status(1)
                .build();
    }

    @Transactional
    @Override
    public void createTestCases(List<TestCaseCreateRequest> requests, int problemId) {
        Problem problem = problemRepo.findById(problemId)
                .orElseThrow(() -> new ResourceNotFoundException("Problem", "id", String.valueOf(problemId)));

        log.info("Test cases: {}", requests);

        List<TestCase> testCases = requests.stream()
                .map(t -> TestCaseMapper.mapToTestCase(t, problem))
                .toList();

        testCaseRepo.saveAll(testCases);
    }

    @Override
    public ListTestCaseResponse getTestCaseByProblemId(int problemId, PagingSearch pagingSearch) {
        Page<TestCase> testCases = testCaseRepo.findAllByProblemId(problemId, pagingSearch.toPageable());

        List<TestCase> testCaseList = testCases.getContent();

        log.info("Test cases: {}", testCaseList);

        List<TestCaseResponse> testCaseResponses = testCaseList.stream()
                .map(t -> TestCaseResponse.builder()
                        .testCaseId(t.getTestCaseId())
                        .input(t.getInput())
                        .output(t.getOutput())
                        .problemId(t.getProblem().getProblemId())
                        .build())
                .toList();

        log.info("Test caseResponses: {}", testCaseResponses);

        ListTestCaseResponse listTestCaseResponse = ListTestCaseResponse.builder()
                .testCases(testCaseResponses)
                .page(testCases.getNumber()+1)
                .totalPages(testCases.getTotalPages())
                .build();
        log.info("Test caseResponse: {}", listTestCaseResponse);
        return listTestCaseResponse;
    }

    @Transactional
    @Override
    public void updateTestCase(Integer testCaseId, TestCaseUpdateRequest request) {
        TestCase existingTestCase = testCaseRepo.findById(testCaseId)
                .orElseThrow(() -> new ResourceNotFoundException("TestCase", "id", String.valueOf(testCaseId)));

        existingTestCase.setInput(request.getInput() == null ? existingTestCase.getInput() : request.getInput().trim());
        existingTestCase.setOutput(request.getOutput() == null ? existingTestCase.getOutput() : request.getOutput().trim());

        testCaseRepo.save(existingTestCase);
    }

    @Transactional
    @Override
    public void deleteAllTestCaseByProblemId(int problemId) {
        List<TestCase> testCases = testCaseRepo.findAllByProblemId(problemId);
        testCaseRepo.deleteAll(testCases);
    }

    @Transactional
    @Override
    public void deleteTestCaseById(int testCaseId) {
        testCaseRepo.deleteById(testCaseId);
    }

    @Override
    public TestCaseResponse getTestCaseById(int testCaseId) {
        TestCase testCase = testCaseRepo.findById(testCaseId)
                .orElseThrow(() -> new ResourceNotFoundException("TestCase", "id", String.valueOf(testCaseId)));
        log.info("Test case: {}", testCase);
        TestCaseResponse  testCaseResponse = TestCaseResponse.builder()
                .testCaseId(testCaseId)
                .input(testCase.getInput())
                .output(testCase.getOutput())
                .problemId(testCase.getProblem().getProblemId())
                .build();
        log.info("Test caseResponse: {}", testCaseResponse);
        return testCaseResponse;
    }

    @Override
    public ListTestCaseResponse getAllTestCases(Integer problemId, PagingSearch pagingSearch) {
        log.info("problemId: {}", problemId);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<TestCase> query = cb.createQuery(TestCase.class);
        Root<TestCase> root = query.from(TestCase.class);
        List<Predicate> predicates = new ArrayList<>();

        if (problemId != 0) {
            log.info("problemId: {}", problemId);
            predicates.add(cb.equal(root.get("problem").get("problemId"), problemId));
        }

        query.select(root)
                .where(cb.and(predicates.toArray(new Predicate[0])))
                .orderBy(cb.asc(root.get("testCaseId")));

        TypedQuery<TestCase> typedQuery = entityManager.createQuery(query);

        int page = pagingSearch.getPage();
        int size = pagingSearch.getSize();

        typedQuery.setFirstResult(page * size);
        typedQuery.setMaxResults(size);

        List<TestCase> testCases = typedQuery.getResultList();

        List<TestCaseResponse> testCaseResponses = testCases.stream()
                .map(t -> TestCaseResponse.builder()
                        .problemId(t.getProblem().getProblemId())
                        .input(t.getInput())
                        .output(t.getOutput())
                        .testCaseId(t.getTestCaseId())
                        .build())
                .toList();

        log.info("size: {}", testCaseResponses.size());

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<TestCase> countRoot = countQuery.from(TestCase.class);

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

        return ListTestCaseResponse.builder()
                .totalPages(totalPages)
                .page(page)
                .testCases(testCaseResponses)
                .build();
    }



}
