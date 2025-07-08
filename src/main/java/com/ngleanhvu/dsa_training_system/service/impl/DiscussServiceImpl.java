package com.ngleanhvu.dsa_training_system.service.impl;

import com.ngleanhvu.dsa_training_system.dto.request.DiscussCreateRequest;
import com.ngleanhvu.dsa_training_system.dto.response.DiscussResponse;
import com.ngleanhvu.dsa_training_system.dto.response.PagingSearch;
import com.ngleanhvu.dsa_training_system.entity.Discuss;
import com.ngleanhvu.dsa_training_system.entity.DiscussTag;
import com.ngleanhvu.dsa_training_system.entity.Tag;
import com.ngleanhvu.dsa_training_system.repo.DiscussRepo;
import com.ngleanhvu.dsa_training_system.repo.DiscussTagRepo;
import com.ngleanhvu.dsa_training_system.repo.TagRepo;
import com.ngleanhvu.dsa_training_system.service.DiscussService;
import com.ngleanhvu.dsa_training_system.util.AppUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiscussServiceImpl implements DiscussService {

    private final DiscussRepo discussRepo;
    private final TagRepo tagRepo;
    private final DiscussTagRepo discussTagRepo;

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
    public List<DiscussResponse> getDiscusses(String keyword, PagingSearch pagingSearch) {
        log.info("keyword: {}", keyword);
        Page<Discuss> discusses = discussRepo.findDiscusses(keyword, pagingSearch.toPageable());
        log.info("discusses: {}", discusses);
        List<DiscussResponse> discussResponses = discusses.stream()
                .map(d -> DiscussResponse.builder()
                        .title(d.getTitle())
                        .content(d.getContent())
                        .createdAt(d.getCreatedAt())
                        .upVotes(d.getUpVotes())
                        .downVotes(d.getDownVotes())
                        .views(d.getViews())
                        .userAvatar(d.getUser().getAvatar())
                        .userEmail(d.getUser().getEmail())
                        .userDisplayName(d.getUser().getDisplayName())
                        .build())
                .toList();
        log.info("discussResponses: {}", discussResponses);
        return discussResponses;
    }
}
