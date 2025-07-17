package com.ngleanhvu.dsa_training_system.repo.spec;

import com.ngleanhvu.dsa_training_system.dto.request.RangeRequest;
import com.ngleanhvu.dsa_training_system.entity.Contest;
import com.ngleanhvu.dsa_training_system.entity.ContestStatus;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ContestSpecification {
    public static Specification<Contest> hashContestId(Integer contestId) {
        return ((root, query, cb) -> {
            if (contestId == null) {
                return null;
            }
            return cb.equal(root.get("contestId"), contestId);
        });
    }

    public static Specification<Contest> hasTitle(String title) {
        return ((root, query, cb) -> {
            if (title == null) {
                return null;
            }
            return cb.like(cb.lower(root.get("title")), "%" + title.trim().toLowerCase() + "%");
        });
    }

    public static Specification<Contest> hasTimestamp(RangeRequest<LocalDate> timestamp) {
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

    public static Specification<Contest> hasStatus(String status) {
        return ((root, query, cb) -> {
            if (status == null) {
                return null;
            }
            ContestStatus contestStatus = ContestStatus.valueOf(status.toUpperCase());
            return cb.equal(root.get("contestStatus"), contestStatus);
        });
    }
}
