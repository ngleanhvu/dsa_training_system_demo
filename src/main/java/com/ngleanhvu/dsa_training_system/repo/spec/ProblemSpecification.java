package com.ngleanhvu.dsa_training_system.repo.spec;

import com.ngleanhvu.dsa_training_system.dto.request.RangeRequest;
import com.ngleanhvu.dsa_training_system.entity.Problem;
import com.ngleanhvu.dsa_training_system.entity.ProblemTopic;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Join;
import java.time.LocalDate;
import java.util.List;

public class ProblemSpecification {

    public static Specification<Problem> hasTitle(String title) {
        return (root, query, cb) -> {
            if (title == null || title.trim().isEmpty()) return null;

            String likePattern = "%" + title.trim().toLowerCase() + "%";
            return cb.like(cb.lower(root.get("title")), likePattern);
        };
    }

    public static Specification<Problem> hasDifficulty(Integer difficultyId) {
        return (root, query, cb) -> {
            if (difficultyId == null) return null;
            return cb.equal(root.get("difficulty").get("difficultyId"), difficultyId);
        };
    }

    public static Specification<Problem> hasTopic(List<Integer> topicIds) {
        return (root, query, cb) -> {
            if (topicIds == null || topicIds.isEmpty()) return null;

            Join<Problem, ProblemTopic> topicJoin = root.join("problemTopics");
            return topicJoin.get("topic").get("topicId").in(topicIds);
        };
    }

    public static Specification<Problem> hasQuestionId(RangeRequest<Integer> questionIdRange) {
        return (root, query, cb) -> {
            if (questionIdRange == null) return null;

            if (questionIdRange.getFrom() != null && questionIdRange.getTo() != null) {
                return cb.between(root.get("problemId"), questionIdRange.getFrom(), questionIdRange.getTo());
            } else if (questionIdRange.getFrom() != null) {
                return cb.greaterThanOrEqualTo(root.get("problemId"), questionIdRange.getFrom());
            } else if (questionIdRange.getTo() != null) {
                return cb.lessThanOrEqualTo(root.get("problemId"), questionIdRange.getTo());
            } else {
                return null;
            }
        };
    }

    public static Specification<Problem> hasPublishDate(RangeRequest<LocalDate> publishDate) {
        return (root, query, cb) -> {
            if (publishDate == null) return null;

            if (publishDate.getFrom() != null && publishDate.getTo() != null) {
                return cb.between(
                        root.get("createdAt"),
                        publishDate.getFrom().atStartOfDay(),
                        publishDate.getTo().atTime(23, 59, 59)
                );
            } else if (publishDate.getFrom() != null) {
                return cb.greaterThanOrEqualTo(root.get("createdAt"), publishDate.getFrom().atStartOfDay());
            } else if (publishDate.getTo() != null) {
                return cb.lessThanOrEqualTo(root.get("createdAt"), publishDate.getTo().atTime(23, 59, 59));
            } else {
                return null;
            }
        };
    }
}
