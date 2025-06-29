package com.ngleanhvu.dsa_training_system.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.ngleanhvu.dsa_training_system.dto.request.SubmissionRequest;
import com.ngleanhvu.dsa_training_system.dto.response.SubmissionResponse;
import com.ngleanhvu.dsa_training_system.entity.ProgrammingLanguage;
import com.ngleanhvu.dsa_training_system.exception.ResourceNotFoundException;
import com.ngleanhvu.dsa_training_system.repo.ProgrammingLanguageRepo;
import com.ngleanhvu.dsa_training_system.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SubmissionServiceImpl implements SubmissionService {

    @Value("${judge0.api_key}")
    private String apiKey;

    @Value("${judge0.api_host}")
    private String apiHost;

    private static final String JUDGE_URL = "https://judge0-ce.p.rapidapi.com/submissions";
    private final RestTemplate restTemplate;
    private final ProgrammingLanguageRepo programmingLanguageRepo;

    @Override
    public String submitSubmission(SubmissionRequest request) {
        ProgrammingLanguage p = programmingLanguageRepo.findById(request.getLanguageId())
                .orElseThrow(() -> new ResourceNotFoundException("Programming language","id",String.valueOf(request.getLanguageId())));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-RapidAPI-Key", apiKey);
        headers.set("X-RapidAPI-Host", apiHost);

        Map<String, Object> body = Map.of(
                "source_code", request.getSourceCode(),
                "language_id", p.getJudge0LanguageId(),
                "stdin", request.getStdin()
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<JsonNode> response = restTemplate.postForEntity( JUDGE_URL + "?base64_encoded=false&wait=false", entity, JsonNode.class);
        return Objects.requireNonNull(response.getBody()).get("token").asText();

    }

    @Override
    public SubmissionResponse getSubmissionResult(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", apiHost);
        headers.set("X-RapidAPI-Host", apiHost);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = JUDGE_URL + "/" + token + "?base64_encoded=false";

        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);


        JsonNode body = response.getBody();
        if (body != null) {
            SubmissionResponse result = new SubmissionResponse();
            result.setStdout(body.path("stdout").asText());
            result.setStderr(body.path("stderr").asText());
            result.setCompile_output(body.path("compile_output").asText());
            result.setStatusId(body.path("status").path("id").asInt());
            result.setStatusDescription(body.path("status").path("description").asText());
            return result;
        }
        return null;
    }
}
