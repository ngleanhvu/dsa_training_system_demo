package com.ngleanhvu.dsa_training_system.service.impl;

import com.ngleanhvu.dsa_training_system.dto.request.DiscussCreateRequest;
import com.ngleanhvu.dsa_training_system.dto.request.DiscussFilterRequest;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import com.ngleanhvu.dsa_training_system.dto.response.SolutionResponse;
import com.ngleanhvu.dsa_training_system.entity.*;
import com.ngleanhvu.dsa_training_system.exception.ResourceNotFoundException;
import com.ngleanhvu.dsa_training_system.repo.*;
import com.ngleanhvu.dsa_training_system.service.SolutionService;
import com.ngleanhvu.dsa_training_system.util.AppUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SolutionServiceImpl implements SolutionService {

    private final DiscussRepo discussRepo;
    private final ProblemRepo problemRepo;
    private final TagRepo tagRepo;
    private final DiscussTagRepo discussTagRepo;
    private final SolutionRepo solutionRepo;

    @Transactional
    @Override
    public void createSolution(Integer problemId, DiscussCreateRequest request) {
        Problem problem = problemRepo.findById(problemId)
                .orElseThrow(() -> new ResourceNotFoundException("Problem", "problemId", String.valueOf(problemId)));

        log.info("request: {}", request);

        Discuss discuss = Discuss.builder()
                .title(request.getTitle())
                .content(AppUtil.sanitize(request.getContent()))
                .status(1)
                .build();

        discussRepo.save(discuss);

        if (request.getTagIds() == null || request.getTagIds().isEmpty()) {
            return;
        }

        List<Tag> tags = tagRepo.findAllById(request.getTagIds());

        List<DiscussTag> discussTags = new ArrayList<>();

        for (Tag tag : tags) {
            DiscussTag discussTag = new DiscussTag();
            discussTag.setDiscuss(discuss);
            discussTag.setTag(tag);
            discussTag.setStatus(1);
            discussTags.add(discussTag);
        }

        discussTagRepo.saveAll(discussTags);

        Solution solution = Solution.builder()
                .status(1)
                .discuss(discuss)
                .problem(problem)
                .build();

        solutionRepo.save(solution);
    }

    @Override
    public List<SolutionResponse> getSolutions(DiscussFilterRequest discussFilterRequest, PagingSearch pagingSearch) {

    }
}
