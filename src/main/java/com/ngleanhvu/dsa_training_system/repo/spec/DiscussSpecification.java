package com.ngleanhvu.dsa_training_system.repo.spec;

import com.ngleanhvu.dsa_training_system.dto.request.RangeRequest;
import com.ngleanhvu.dsa_training_system.entity.Discuss;
import com.ngleanhvu.dsa_training_system.entity.DiscussTag;
import com.ngleanhvu.dsa_training_system.entity.Solution;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DiscussSpecification {

    public static Specification<Discuss> hasKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.trim().isEmpty()) {
                return cb.conjunction();
            }

            String likePattern = "%" + keyword.trim().toLowerCase() + "%";
            Path<String> titlePath = root.get("title");
            return cb.like(cb.lower(titlePath), likePattern);
        };
    }

    public static Specification<Discuss> hasTimestamp(RangeRequest<LocalDate> timestamp) {
        return (root, query, cb) -> {
            if (timestamp == null || (timestamp.getFrom() == null && timestamp.getTo() == null)) {
                return cb.conjunction();
            }

            Path<LocalDateTime> createdAt = root.get("createdAt");
            List<Predicate> predicates = new ArrayList<>();

            if (timestamp.getFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(createdAt, timestamp.getFrom().atStartOfDay()));
            }
            if (timestamp.getTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(createdAt, timestamp.getTo().atTime(23, 59, 59)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Discuss> hasTag(List<Integer> tagIds) {
        return (root, query, cb) -> {
            if (tagIds == null || tagIds.isEmpty()) {
                return cb.conjunction();
            }

            query.distinct(true);
            Join<Discuss, DiscussTag> tagJoin = root.join("discussTags", JoinType.INNER);
            return tagJoin.get("tag").get("tagId").in(tagIds);
        };
    }

    public static Specification<Discuss> hasProblem(Integer problemId) {
        return (root, query, cb) -> {
            if (problemId == null) {
                return cb.conjunction();
            }

            Join<Discuss, Solution> solutionJoin = root.join("solution", JoinType.INNER);
            return cb.equal(solutionJoin.get("problem").get("problemId"), problemId);
        };
    }

}
