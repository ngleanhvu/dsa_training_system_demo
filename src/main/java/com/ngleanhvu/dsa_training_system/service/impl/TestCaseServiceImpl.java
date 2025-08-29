package com.ngleanhvu.dsa_training_system.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestCaseServiceImpl implements TestCaseService {

    private final TestCaseRepo testCaseRepo;
    private final ProblemRepo problemRepo;
    private final ObjectMapper objectMapper;

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

        List<TestCase> testCases = requests.stream()
                .map(t -> TestCaseMapper.mapToTestCase(t, problem))
                .toList();

        testCaseRepo.saveAll(testCases);
    }

    @Override
    public ListTestCaseResponse getTestCaseByProblemId(int problemId, PagingSearch pagingSearch) {
        Page<TestCase> testCases = testCaseRepo.findAllByProblemId(problemId, pagingSearch.toPageable());

        List<TestCase> testCaseList = testCases.getContent();

        List<TestCaseResponse> testCaseResponses = testCaseList.stream()
                .map(t -> TestCaseResponse.builder()
                        .testCaseId(t.getTestCaseId())
                        .input(t.getInput())
                        .output(t.getOutput())
                        .problemId(t.getProblem().getProblemId())
                        .build())
                .toList();


        return ListTestCaseResponse.builder()
                .testCases(testCaseResponses)
                .page(testCases.getNumber()+1)
                .totalPages(testCases.getTotalPages())
                .build();
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

        return TestCaseResponse.builder()
                .testCaseId(testCaseId)
                .input(testCase.getInput())
                .output(testCase.getOutput())
                .problemId(testCase.getProblem().getProblemId())
                .build();
    }

    @Override
    public ListTestCaseResponse getAllTestCases(Integer problemId, PagingSearch pagingSearch) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<TestCase> query = cb.createQuery(TestCase.class);
        Root<TestCase> root = query.from(TestCase.class);
        List<Predicate> predicates = new ArrayList<>();

        if (problemId != 0) {
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


        return ListTestCaseResponse.builder()
                .totalPages(totalPages)
                .page(page)
                .testCases(testCaseResponses)
                .build();
    }

    @Transactional
    @Override
    public void uploadTestCase(Integer problemId, MultipartFile file) throws IOException {
        Problem problem = problemRepo.findById(problemId)
                .orElseThrow(() -> new ResourceNotFoundException("Problem", "id", String.valueOf(problemId)));

        JsonNode root = objectMapper.readTree(file.getInputStream());

        List<TestCase> testCases = new ArrayList<>();

        if (root.isArray()) {
            // Trường hợp file là danh sách nhiều test case
            for (JsonNode node : root) {
                testCases.add(buildTestCase(node, problem));
            }
        } else if (root.isObject()) {
            // Trường hợp file chỉ có 1 test case
            testCases.add(buildTestCase(root, problem));
        } else {
            throw new IllegalArgumentException("Invalid JSON format for test case file");
        }

        testCaseRepo.saveAll(testCases);
    }

    private TestCase buildTestCase(JsonNode node, Problem problem) {
        TestCase testCase = new TestCase();
        testCase.setInput(flatten(node.get("input")));
        testCase.setOutput(flatten(node.get("output")));
        testCase.setProblem(problem);

        return testCase;
    }

    private String flatten(JsonNode root) {
        if (root == null || root.isNull()) {
            return "";
        }
        List<String> values = new ArrayList<>();
        collectValues(root, values);
        return String.join("\n", values);
    }

    private void collectValues(JsonNode node, List<String> result) {
        if (node.isArray()) {
            boolean allPrimitive = true;
            List<String> values = new ArrayList<>();
            for (JsonNode val : node) {
                if (val.isContainerNode()) {
                    allPrimitive = false;
                    break;
                } else {
                    values.add(val.asText());
                }
            }

            if (allPrimitive) {
                // mảng 1 chiều → gom thành 1 dòng
                result.add(String.join(" ", values));
            } else {
                // mảng nhiều chiều → duyệt tiếp từng phần tử
                for (JsonNode val : node) {
                    collectValues(val, result);
                }
            }

        } else if (node.isObject()) {
            node.fields().forEachRemaining(e -> collectValues(e.getValue(), result));
        } else if (node.isValueNode()) {
            // giá trị primitive → 1 dòng
            result.add(node.asText());
        }
    }

}
