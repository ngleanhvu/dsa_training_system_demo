package com.ngleanhvu.dsa_training_system.service.impl;

import com.ngleanhvu.dsa_training_system.dto.request.DiscussCreateRequest;
import com.ngleanhvu.dsa_training_system.dto.request.DiscussFilterRequest;
import com.ngleanhvu.dsa_training_system.dto.request.DiscussUpdateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.*;
import com.ngleanhvu.dsa_training_system.entity.*;
import com.ngleanhvu.dsa_training_system.exception.PermissionException;
import com.ngleanhvu.dsa_training_system.exception.ResourceNotFoundException;
import com.ngleanhvu.dsa_training_system.mappter.DiscussMapper;
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

import java.time.LocalDateTime;
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

        User user = userRepo.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", request.getUserId()));

        log.info("request: {}", request);

        Discuss discuss = Discuss.builder()
                .title(request.getTitle())
                .content(AppUtil.sanitize(request.getContent()))
                .upVotes(0)
                .views(0)
                .commentCount(0)
                .status(1)
                .user(user)
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
    public ListDiscussResponse getDiscusses(DiscussFilterRequest discussFilterRequest, PagingSearch pagingSearch) {
        Specification<Discuss> spec = DiscussSpecification.hasKeyword(discussFilterRequest.getKeyword())
                .and(DiscussSpecification.hasTimestamp(discussFilterRequest.getTimestamp()))
                .and(DiscussSpecification.hasTag(discussFilterRequest.getTagIds()));

        Page<Discuss> discussPage = discussRepo.findAll(spec, pagingSearch.toPageable());
        List<DiscussResponse> discussResponses = discussPage.stream()
                .map(DiscussMapper::toDto)
                .toList();
        log.info("discussResponses: {}", discussResponses);
        int totalPages = discussPage.getTotalPages();
        int page = discussPage.getNumber() + 1;
        return ListDiscussResponse.builder()
                .discuss(discussResponses)
                .totalPages(totalPages)
                .page(page)
                .build();
    }


    @Override
    public DiscussDetailResponse getDiscussById(Integer discussId) {
        Discuss d = discussRepo.findById(discussId)
                .orElseThrow(() -> new ResourceNotFoundException("Discuss","id",String.valueOf(discussId)));

        DiscussDetailResponse discussResponse = DiscussMapper.toDiscussDetailResponse(d);

        log.info("discussResponse: {}", discussResponse);

        return discussResponse;
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
            discuss.setUpVotes(Math.max(0, discuss.getUpVotes() - 1));
        } else {
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User","id", (userId)));
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
    public void deleteDiscuss(String userId,
                              Integer discussId) {
        Discuss discuss = discussRepo.findById(discussId)
                .orElseThrow(() -> new ResourceNotFoundException("Discuss","id", String.valueOf(discussId)));

        if (!discuss.getUser().getUserId().equals(userId)) {
            throw new PermissionException("Discuss", String.valueOf(discussId));
        }

        discussRepo.delete(discuss);
    }

    @Transactional
    @Override
    public void updateDiscuss(Integer discussId, DiscussUpdateRequest request) {
        Discuss discuss = discussRepo.findById(discussId)
                .orElseThrow(() -> new ResourceNotFoundException("Discuss", "id", String.valueOf(discussId)));
        log.info("discuss: {}", discuss);
        log.info("updateDiscuss: {}", request);
        log.info("userId: {}", discuss.getUser().getUserId());
        log.info("userId with discuss request: {}", request.getUserId());
        if (!discuss.getUser().getUserId().equals(request.getUserId()) && discuss.getUser().getRole() != UserRole.ADMIN) {
            throw new PermissionException("Discuss",String.valueOf(discussId));
        }

        discuss.setTitle(request.getTitle());
        discuss.setContent(request.getContent());

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

    @Override
    public DiscussForUpdateResponse getDiscussForUpdate(Integer discussId) {
        Discuss discuss = discussRepo.findById(discussId)
                .orElseThrow(() -> new ResourceNotFoundException("Discuss","id", String.valueOf(discussId)));

        DiscussForUpdateResponse response = new DiscussForUpdateResponse();
        response.setTitle(discuss.getTitle());
        response.setContent(discuss.getContent());

        List<Integer> tagIds = discuss.getDiscussTags().stream()
                .map(d -> d.getTag().getTagId())
                .toList();

        response.setTagIds(tagIds);
        log.info("discuss response: {}", response);

        return response;
    }

    @Override
    public ListDiscussResponse getDiscussesWithUser(DiscussFilterRequest discussFilterRequest, String userId, PagingSearch pagingSearch) {
        Page<Object[]> discussesWithUser = discussRepo.findDiscussesWithUser(discussFilterRequest.getKeyword(),
                userId,
                pagingSearch.toPageable());

        log.info("total elements: {}", discussesWithUser.getTotalElements());

        List<DiscussResponse> discussDetailResponses = discussesWithUser.getContent().stream()
                .map(d -> DiscussResponse.builder()
                        .discussId((Integer) d[0])
                        .title((String) d[1])
                        .content((String) d[2])
                        .createdAt(AppUtil.changeFormatDate(d[3]))
                        .upVotes((Integer) d[4])
                        .comments((Integer) d[5])
                        .userEmail((String) d[6])
                        .userDisplayName((String) d[7])
                        .userAvatar((String) d[8])
                        .isUpVote((Long) d[9])
                        .build())
                .toList();

        log.info("discuss response: {}", discussDetailResponses);

        return ListDiscussResponse.builder()
                .discuss(discussDetailResponses)
                .totalPages(discussesWithUser.getTotalPages())
                .page(discussesWithUser.getNumber()+1)
                .build();
    }

    @Override
    public DiscussDetailResponse getDiscussDetail(Integer discussId, String userId) {
        List<Object[]> discuss = discussRepo.findByDiscussWithCredential(discussId, userId);
        if (discuss.isEmpty()) {
            throw new ResourceNotFoundException("Discuss", "id", String.valueOf(discussId));
        }

        Object[] row = discuss.getFirst();
        log.info("discuss response: {}", row);
        try {
            System.out.println(row[9].getClass().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String title = (String) row[1];
        log.info("discuss title: {}", title);
        String content = (String) row[2];
        log.info("discuss content: {}", content);
        LocalDateTime createdAt = AppUtil.changeFormatDate(row[3]);
        log.info("discuss createdAt: {}", createdAt);
        Integer upVotes = (Integer) row[5];
        log.info("discuss upVotes: {}", upVotes);
        Integer comments = (Integer) row[4];
        log.info("discuss comments: {}", comments);
        String userEmail = (String) row[6];
        log.info("discuss userEmail: {}", userEmail);
        String userDisplayName = (String) row[7];
        log.info("discuss userDisplayName: {}", userDisplayName);
        String userAvatar = (String) row[8];
        log.info("discuss userAvatar: {}", userAvatar);
        Long isUpVote = (Long) row[9];
        log.info("discuss isUpVote: {}", isUpVote);

        List<TagResponse> tagResponses = discussTagRepo.findByDiscussId(discussId).stream()
                .map(t -> TagResponse.builder().tagId(t.getTag().getTagId())
                        .name(t.getTag().getName())
                        .build())
                .toList();

        DiscussDetailResponse discussDetailResponse = DiscussDetailResponse.builder()
                        .discussId(discussId)
                        .title(title)
                        .content(content)
                        .createdAt(createdAt)
                        .upVotes(upVotes)
                        .tags(tagResponses)
                        .comments(comments)
                        .userEmail(userEmail)
                        .userDisplayName(userDisplayName)
                        .userAvatar(userAvatar)
                        .isUpVote(isUpVote)
                        .build();

        log.info("discuss response: {}", discussDetailResponse);

        return discussDetailResponse;

    }
}
