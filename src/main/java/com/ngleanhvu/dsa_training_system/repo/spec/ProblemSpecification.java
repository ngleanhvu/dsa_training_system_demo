package com.ngleanhvu.dsa_training_system.repo.spec;

import com.ngleanhvu.dsa_training_system.dto.request.RangeRequest;
import com.ngleanhvu.dsa_training_system.entity.Problem;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

public class ProblemSpecification {

    public static Specification<Problem> hasTitle(String title) {
        return (root, query, criteriaBuilder) ->
                (title == null || title.trim().isEmpty())
                        ? null
                        : criteriaBuilder.like(root.get("title"), "%" + title.trim() + "%");
    }

    public static Specification<Problem> hasDifficulty(Integer difficultyId) {
        return (root, query, criteriaBuilder) ->
                difficultyId == null
                        ? null
                        : criteriaBuilder.equal(root.get("difficulty").get("difficultyId"), difficultyId);
    }

    public static Specification<Problem> hasTopic(List<Integer> topicIds) {
        return (root, query, criteriaBuilder) -> {
            if (topicIds == null || topicIds.isEmpty()) {
                return null;
            }
            return root.get("problemTopics").get("topic").get("topicId").in(topicIds);
        };
    }

    public static Specification<Problem> hasQuestionId(RangeRequest<Integer> questionIdRange) {
        return (root, query, criteriaBuilder) -> {
            if (questionIdRange == null || questionIdRange.getFrom() == null || questionIdRange.getTo() == null) {
                return null;
            }
            return criteriaBuilder.between(
                    root.get("problemId"),
                    questionIdRange.getFrom(),
                    questionIdRange.getTo()
            );
        };
    }

    public static Specification<Problem> hasPublishDate(RangeRequest<LocalDate> publishDate) {
        return (root, query, criteriaBuilder) -> {
            if (publishDate == null || publishDate.getFrom() == null || publishDate.getTo() == null) {
                return null;
            }
            return criteriaBuilder.between(
                    root.get("createdAt"),
                    publishDate.getFrom().atStartOfDay(),
                    publishDate.getTo().atTime(23, 59, 59)
            );
        };
    }
}
