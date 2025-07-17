package com.ngleanhvu.dsa_training_system.service.impl;

import com.ngleanhvu.dsa_training_system.dto.request.DiscussCreateRequest;
import com.ngleanhvu.dsa_training_system.dto.request.DiscussFilterRequest;
import com.ngleanhvu.dsa_training_system.dto.request.DiscussUpdateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.DiscussResponse;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import com.ngleanhvu.dsa_training_system.entity.*;
import com.ngleanhvu.dsa_training_system.exception.ResourceNotFoundException;
import com.ngleanhvu.dsa_training_system.repo.*;
import com.ngleanhvu.dsa_training_system.repo.spec.DiscussSpecification;
import com.ngleanhvu.dsa_training_system.service.DiscussService;
import com.ngleanhvu.dsa_training_system.util.AppUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiscussServiceImpl implements DiscussService {

    private final DiscussRepo discussRepo;
    private final TagRepo tagRepo;
    private final DiscussTagRepo discussTagRepo;
    private final DiscussVoteRepo discussVoteRepo;
    private final UserRepo userRepo;

    @Transactional
    @Override
    public void createDiscuss(DiscussCreateRequest request) {
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
    }

    @Override
    public List<DiscussResponse> getDiscusses(DiscussFilterRequest discussFilterRequest, PagingSearch pagingSearch) {
        Specification<Discuss> specification = DiscussSpecification.hasKeyword(discussFilterRequest.getKeyword())
                        .and(DiscussSpecification.hasTag(discussFilterRequest.getTagIds())
                        .and(DiscussSpecification.hasTimestamp(discussFilterRequest.getTimestamp())));
        Page<Discuss> discussPage = discussRepo.findAll(specification, pagingSearch.toPageable());
        List<Discuss> discusses = discussPage.getContent();
        log.info("discusses: {}", discusses);
        List<DiscussResponse> discussResponses = discusses.stream()
                .map(d -> DiscussResponse.builder()
                        .title(d.getTitle())
                        .content(d.getContent())
                        .createdAt(d.getCreatedAt())
                        .upVotes(d.getUpVotes())
                        .views(d.getViews())
                        .userAvatar(d.getUser().getAvatar())
                        .userEmail(d.getUser().getEmail())
                        .userDisplayName(d.getUser().getDisplayName())
                        .build())
                .toList();
        log.info("discussResponses: {}", discussResponses);
        return discussResponses;
    }

    @Transactional
    @Override
    public void toggleVote(String userId,
                           Integer discussId) {
        Discuss discuss = discussRepo.findById(discussId)
                .orElseThrow(() -> new ResourceNotFoundException("Discuss","id", String.valueOf(discussId)));

        Optional<DiscussVote> discussVoteOptional = discussVoteRepo.findByDiscussId(discussId);

        if (discussVoteOptional.isPresent()) {
            discussVoteRepo.delete(discussVoteOptional.get());
            discuss.setUpVotes(discuss.getUpVotes() - 1);
        } else {
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User","id", String.valueOf(userId)));
            DiscussVote discussVote = new DiscussVote();
            discussVote.setDiscuss(discuss);
            discussVote.setUser(user);
            discussVote.setStatus(1);
            discussVoteRepo.save(discussVote);
            discuss.setUpVotes(discuss.getUpVotes() + 1);
        }
        log.info("discussVote: {}", discuss);
        discussRepo.save(discuss);
    }

    @Transactional
    @Override
    public void deleteDiscuss(Integer discussId) {
        discussRepo.deleteById(discussId);
    }

    @Transactional
    @Override
    public void updateDiscuss(Integer discussId, DiscussUpdateRequest request) {
        Discuss discuss = discussRepo.findById(discussId)
                .orElseThrow(() -> new ResourceNotFoundException("Discuss", "id", String.valueOf(discussId)));

        // Cập nhật title & content
        discuss.setTitle(request.getTitle());
        discuss.setContent(request.getContent());

        // Cập nhật tags nếu có
        if (request.getTagIds() != null) {
            List<Tag> newTags = tagRepo.findAllById(request.getTagIds());
            List<DiscussTag> currentDiscussTags = discussTagRepo.findByDiscussId(discussId);

            Set<Integer> currentTagIds = currentDiscussTags.stream()
                    .map(dt -> dt.getTag().getTagId())
                    .collect(Collectors.toSet());

            Set<Integer> newTagIds = newTags.stream()
                    .map(Tag::getTagId)
                    .collect(Collectors.toSet());

            Set<Integer> toRemove = new HashSet<>(currentTagIds);
            toRemove.removeAll(newTagIds);

            if (!toRemove.isEmpty()) {
                discussTagRepo.deleteByDiscussIdAndTagIds(discussId, toRemove);
            }

            Set<Integer> toAdd = new HashSet<>(newTagIds);
            toAdd.removeAll(currentTagIds);

            if (!toAdd.isEmpty()) {
                List<Tag> tagsToAdd = newTags.stream()
                        .filter(tag -> toAdd.contains(tag.getTagId()))
                        .toList();

                List<DiscussTag> newDiscussTags = tagsToAdd.stream()
                        .map(tag -> DiscussTag.builder()
                                .discuss(discuss)
                                .tag(tag)
                                .status(1)
                                .build())
                        .toList();

                discussTagRepo.saveAll(newDiscussTags);
            }
        }

        discussRepo.save(discuss);
    }


}
