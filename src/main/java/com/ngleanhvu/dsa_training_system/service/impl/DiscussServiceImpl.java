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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final CommentRepo commentRepo;

    @Transactional
    @Override
    public void createDiscuss(DiscussCreateRequest request) {
        User user = getUserOrThrow(request.getUserId());

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

        saveDiscussTags(discuss, request.getTagIds());
    }

    @Override
    public ListDiscussResponse getDiscusses(DiscussFilterRequest filter, PagingSearch paging) {
        Page<Discuss> page = discussRepo.findAll(
                DiscussSpecification.hasKeyword(filter.getKeyword())
                        .and(DiscussSpecification.hasTimestamp(filter.getTimestamp()))
                        .and(DiscussSpecification.hasTag(filter.getTagIds())),
                paging.toPageable()
        );
        return buildListDiscussResponse(page.stream().map(d -> {
            DiscussResponse discussResponse = DiscussMapper.toDto(d);
            long countComment = commentRepo.countCommentByDiscussId(d.getDiscussId());
            discussResponse.setComments((int) countComment);
            return discussResponse;
        }).toList(), page);
    }

    @Override
    public DiscussDetailResponse getDiscussById(Integer discussId) {
        Discuss discuss = getDiscussOrThrow(discussId);
        DiscussDetailResponse response = DiscussMapper.toDiscussDetailResponse(discuss);
        response.setComments((int) commentRepo.countCommentByDiscussId(discussId));
        return response;
    }

    @Transactional
    @Override
    public void toggleVote(String userId, Integer discussId) {
        Discuss discuss = getDiscussOrThrow(discussId);
        Optional<DiscussVote> voteOpt = discussVoteRepo.findByDiscussIdAndUserId(discussId, userId);

        voteOpt.ifPresentOrElse(vote -> {
            discussVoteRepo.delete(vote);
            discuss.setUpVotes(Math.max(0, discuss.getUpVotes() - 1));
        }, () -> {
            User user = getUserOrThrow(userId);
            DiscussVote vote = new DiscussVote();
            vote.setDiscuss(discuss);
            vote.setUser(user);
            vote.setStatus(1);
            discussVoteRepo.save(vote);
            discuss.setUpVotes(discuss.getUpVotes() + 1);
        });

        discussRepo.save(discuss);
    }

    @Transactional
    @Override
    public void deleteDiscuss(String userId, Integer discussId) {
        Discuss discuss = getDiscussOrThrow(discussId);
        checkPermission(discuss.getUser().getUserId(), userId);
        discussRepo.delete(discuss);
    }

    @Transactional
    @Override
    public void updateDiscuss(Integer discussId, DiscussUpdateRequest request) {
        Discuss discuss = getDiscussOrThrow(discussId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<? extends GrantedAuthority> userRole =  authentication.getAuthorities().stream().toList();
        UserRole role = UserRole.valueOf(userRole.getFirst().getAuthority().substring(5));
        if (!discuss.getUser().getUserId().equals(request.getUserId()) &&
                role != UserRole.ADMIN) {
            throw new PermissionException("Discuss", String.valueOf(discussId));
        }

        discuss.setTitle(request.getTitle());
        discuss.setContent(AppUtil.sanitize(request.getContent()));
        updateDiscussTags(discuss, request.getTagIds());
        discussRepo.save(discuss);
    }

    @Override
    public DiscussDetailResponse getDiscussForUpdate(Integer discussId) {
        Discuss discuss = getDiscussOrThrow(discussId);
        DiscussDetailResponse discussDetailResponse = DiscussMapper.toDiscussDetailResponse(discuss);
        discussDetailResponse.setComments((int) commentRepo.countCommentByDiscussId(discussId));
        return discussDetailResponse;
    }

    @Override
    public ListDiscussResponse getDiscussesWithUser(DiscussFilterRequest filter, String userId, PagingSearch paging) {
        Page<Object[]> page = discussRepo.findDiscussesWithUser(filter.getKeyword(), userId, paging.toPageable());
        List<DiscussResponse> responses = page.getContent().stream()
                .map(d -> {
                    DiscussResponse discussResponse = this.mapObjectArrayToDiscussResponse(d);
                    discussResponse.setComments((int) commentRepo.countCommentByDiscussId(discussResponse.getDiscussId()));
                    return discussResponse;
                })
                .toList();
        log.info("responses: {}", responses);
        return buildListDiscussResponse(responses, page);
    }

    @Override
    public DiscussDetailResponse getDiscussDetail(Integer discussId, String userId) {
        List<Object[]> rows = discussRepo.findByDiscussWithCredential(discussId, userId);
        if (rows.isEmpty()) throw new ResourceNotFoundException("Discuss", "id", String.valueOf(discussId));

        Object[] row = rows.getFirst();
        DiscussDetailResponse response = mapObjectArrayToDiscussDetailResponse(row, discussId);
        response.setComments((int) commentRepo.countCommentByDiscussId(discussId));
        List<TagResponse> tags = discussTagRepo.findByDiscussId(discussId).stream()
                .map(t -> TagResponse.builder()
                        .tagId(t.getTag().getTagId())
                        .name(t.getTag().getName())
                        .build())
                .toList();
        response.setTags(tags);

        return response;
    }

    // =================== PRIVATE HELPERS ===================

    private User getUserOrThrow(String userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    private Discuss getDiscussOrThrow(Integer discussId) {
        return discussRepo.findById(discussId)
                .orElseThrow(() -> new ResourceNotFoundException("Discuss", "id", String.valueOf(discussId)));
    }

    private void checkPermission(String ownerId, String userId) {
        if (!ownerId.equals(userId)) throw new PermissionException("Discuss", userId);
    }

    private void saveDiscussTags(Discuss discuss, List<Integer> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) return;
        List<Tag> tags = tagRepo.findAllById(tagIds);
        List<DiscussTag> discussTags = tags.stream()
                .map(tag -> DiscussTag.builder()
                        .discuss(discuss)
                        .tag(tag)
                        .status(1)
                        .build())
                .toList();
        discussTagRepo.saveAll(discussTags);
    }

    private void updateDiscussTags(Discuss discuss, List<Integer> newTagIds) {
        if (newTagIds == null) return;

        List<Tag> newTags = tagRepo.findAllById(newTagIds);
        List<DiscussTag> currentTags = discussTagRepo.findByDiscussId(discuss.getDiscussId());

        Set<Integer> currentIds = currentTags.stream().map(dt -> dt.getTag().getTagId()).collect(Collectors.toSet());
        Set<Integer> newIds = newTags.stream().map(Tag::getTagId).collect(Collectors.toSet());

        // Remove old tags
        Set<Integer> toRemove = new HashSet<>(currentIds);
        toRemove.removeAll(newIds);
        if (!toRemove.isEmpty()) discussTagRepo.deleteByDiscussIdAndTagIds(discuss.getDiscussId(), toRemove);

        // Add new tags
        Set<Integer> toAdd = new HashSet<>(newIds);
        toAdd.removeAll(currentIds);
        if (!toAdd.isEmpty()) {
            List<DiscussTag> tagsToAdd = newTags.stream()
                    .filter(tag -> toAdd.contains(tag.getTagId()))
                    .map(tag -> DiscussTag.builder().discuss(discuss).tag(tag).status(1).build())
                    .toList();
            discussTagRepo.saveAll(tagsToAdd);
        }
    }

    private DiscussResponse mapObjectArrayToDiscussResponse(Object[] obj) {
        return DiscussResponse.builder()
                .discussId((Integer) obj[0])
                .title((String) obj[1])
                .content((String) obj[2])
                .createdAt(AppUtil.changeFormatDate(obj[3]))
                .upVotes((Integer) obj[4])
                .comments((Integer) obj[5])
                .userEmail((String) obj[6])
                .userDisplayName((String) obj[7])
                .userAvatar((String) obj[8])
                .isUpVote((Long) obj[9])
                .build();
    }

    private DiscussDetailResponse mapObjectArrayToDiscussDetailResponse(Object[] obj, Integer discussId) {
        return DiscussDetailResponse.builder()
                .discussId(discussId)
                .title((String) obj[1])
                .content((String) obj[2])
                .createdAt(AppUtil.changeFormatDate(obj[3]))
                .upVotes((Integer) obj[5])
                .comments((Integer) obj[4])
                .userEmail((String) obj[6])
                .userDisplayName((String) obj[7])
                .userAvatar((String) obj[8])
                .isUpVote((Long) obj[9])
                .build();
    }

    private ListDiscussResponse buildListDiscussResponse(List<DiscussResponse> responses, Page<?> page) {
        return ListDiscussResponse.builder()
                .discuss(responses)
                .totalPages(page.getTotalPages())
                .page(page.getNumber() + 1)
                .build();
    }
}
