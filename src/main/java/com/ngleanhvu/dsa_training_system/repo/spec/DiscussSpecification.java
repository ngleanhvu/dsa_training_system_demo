package com.ngleanhvu.dsa_training_system.repo.spec;

import com.ngleanhvu.dsa_training_system.dto.request.RangeRequest;
import com.ngleanhvu.dsa_training_system.entity.Discuss;
import com.ngleanhvu.dsa_training_system.entity.DiscussTag;
import com.ngleanhvu.dsa_training_system.entity.Solution;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DiscussSpecification {
    public static Specification<Discuss> hasKeyword(String keyword) {
        return ((root, query, cb) -> {
           if (keyword == null || keyword.isEmpty()) {
               return null;
           }

           String likePattern = "%" + keyword.trim().toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("discuss").get("title")), likePattern),
                    cb.like(cb.lower(root.get("discuss").get("content")), likePattern)
            );
        });
    }

    public static Specification<Discuss> hasTimestamp(RangeRequest<LocalDate> timestamp) {
        return (root, query, cb) -> {
            if (timestamp == null || (timestamp.getFrom() == null && timestamp.getTo() == null)) return null;

            Path<LocalDateTime> createdAtPath = root.get("createdAt");

            List<Predicate> predicates = new ArrayList<>();

            if (timestamp.getFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(createdAtPath, timestamp.getFrom().atStartOfDay()));
            }
            if (timestamp.getTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(createdAtPath, timestamp.getTo().atTime(23, 59, 59)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Discuss> hasTag(List<Integer> tagIds) {
        return ((root, query, cb) -> {
           if (tagIds == null || tagIds.isEmpty()) {
               return null;
           }

           Join<Discuss, DiscussTag> join = root.join("discussTags", JoinType.INNER);

           return join.get("tag").get("tagId").in(tagIds);
        });
    }

    public static Specification<Discuss> hasProblem(Integer problemId) {
        return (root, query, cb) -> {
            if (problemId == null) return null;

            Join<Discuss, Solution> join = root.join("solution", JoinType.INNER);

            return cb.equal(join.get("problem").get("problemId"), problemId);
        };
    }

}
