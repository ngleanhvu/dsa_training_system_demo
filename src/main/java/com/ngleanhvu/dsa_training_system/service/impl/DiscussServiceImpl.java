package com.ngleanhvu.dsa_training_system.service.impl;

import com.ngleanhvu.dsa_training_system.dto.request.DiscussCreateRequest;
import com.ngleanhvu.dsa_training_system.dto.request.DiscussFilterRequest;
import com.ngleanhvu.dsa_training_system.dto.request.DiscussUpdateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.DiscussResponse;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import com.ngleanhvu.dsa_training_system.entity.*;
import com.ngleanhvu.dsa_training_system.exception.PermissionException;
import com.ngleanhvu.dsa_training_system.exception.ResourceNotFoundException;
import com.ngleanhvu.dsa_training_system.repo.*;
import com.ngleanhvu.dsa_training_system.service.DiscussService;
import com.ngleanhvu.dsa_training_system.util.AppUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @PersistenceContext
    private EntityManager entityManager;

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
    public List<DiscussResponse> getDiscusses(DiscussFilterRequest discussFilterRequest, PagingSearch pagingSearch) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Discuss> cq = cb.createQuery(Discuss.class);
        Root<Discuss> root = cq.from(Discuss.class);
        root.fetch("user", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();

        if (discussFilterRequest.getKeyword() != null && !discussFilterRequest.getKeyword().isBlank()) {
            String likePattern = "%" + discussFilterRequest.getKeyword().toLowerCase() + "%";
            predicates.add(cb.like((cb.lower(root.get("title"))), likePattern));
        }

        if (discussFilterRequest.getTagIds() != null && !discussFilterRequest.getTagIds().isEmpty()) {
            Join<Discuss, DiscussTag> tagJoin = root.join("discussTags", JoinType.INNER);
            predicates.add(tagJoin.get("tag").get("tagId").in(discussFilterRequest.getTagIds()));
            predicates.add(cb.equal(tagJoin.get("status"), 1));
        }

        if (discussFilterRequest.getTimestamp() != null) {
            predicates.add(
                    cb.between(
                            root.get("createdAt"),
                            discussFilterRequest.getTimestamp().getFrom(),
                            discussFilterRequest.getTimestamp().getTo()
                    )
            );
        }

        cq.where(cb.and(predicates.toArray(new Predicate[0])));
        cq.orderBy(cb.desc(root.get("createdAt")));

        TypedQuery<Discuss> query = entityManager.createQuery(cq);
        query.setFirstResult(pagingSearch.getPage() * pagingSearch.getSize());
        query.setMaxResults(pagingSearch.getSize());

        List<Discuss> results = query.getResultList();

        return results.stream()
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
    }


    @Override
    public DiscussResponse getDiscussById(Integer discussId) {
        Discuss d = discussRepo.findById(discussId)
                .orElseThrow(() -> new ResourceNotFoundException("Discuss","id",String.valueOf(discussId)));

        DiscussResponse discussResponse = DiscussResponse.builder()
                .title(d.getTitle())
                .content(d.getContent())
                .createdAt(d.getCreatedAt())
                .upVotes(d.getUpVotes())
                .views(d.getViews())
                .userAvatar(d.getUser().getAvatar())
                .userEmail(d.getUser().getEmail())
                .userDisplayName(d.getUser().getDisplayName())
                .build();

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
            discuss.setUpVotes(discuss.getUpVotes() - 1);
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

        if (!discuss.getUser().getUserId().equals(request.getUserId())) {
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
}
