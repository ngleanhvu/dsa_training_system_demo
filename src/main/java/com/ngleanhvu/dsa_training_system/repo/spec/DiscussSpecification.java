package com.ngleanhvu.dsa_training_system.repo.spec;

import com.ngleanhvu.dsa_training_system.dto.request.RangeRequest;
import com.ngleanhvu.dsa_training_system.entity.Discuss;
import com.ngleanhvu.dsa_training_system.entity.DiscussTag;
import com.ngleanhvu.dsa_training_system.entity.Solution;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

public class DiscussSpecification {
    public static Specification<Discuss> hasKeyword(String keyword) {
        return ((root, query, criteriaBuilder) -> {
           if (keyword == null || keyword.isEmpty()) {
               return null;
           }

           criteriaBuilder.like(root.get("title"), "%" + keyword +"%");
           criteriaBuilder.like(root.get("content"), "%" + keyword +"%");
           return criteriaBuilder.conjunction();
        });
    }

    public static Specification<Discuss> hasTimestamp(RangeRequest<LocalDate> timestamp) {
        return ((root, query, criteriaBuilder) -> {
           if (timestamp.getFrom() == null || timestamp.getFrom().isBefore(LocalDate.now())) {
               return null;
           }

           if (timestamp.getTo() == null || timestamp.getTo().isAfter(LocalDate.now())) {
               return null;
           }

           return criteriaBuilder.between(root.get("createdAt"),
                   timestamp.getFrom(),
                   timestamp.getTo());
        });
    }

    public static Specification<Discuss> hasTag(List<Integer> tagIds) {
        return ((root, query, criteriaBuilder) -> {
           if (tagIds == null || tagIds.isEmpty()) {
               return null;
           }

           Join<Discuss, DiscussTag> join = root.join("discussTags", JoinType.INNER);

           return join.get("tag").get("tagId").in(tagIds);
        });
    }

    public static Specification<Discuss> hasProblem(Integer problemId) {
        return ((root, query, criteriaBuilder) -> {
           if (problemId == null || problemId < 0) {
               return null;
           }

           Join<Discuss, Solution> join = root.join("solution", JoinType.INNER);
           return criteriaBuilder.equal(join.get("solution").get("problem").get("problemId"), problemId);
        });
    }
}
