package com.ngleanhvu.dsa_training_system.repo.spec;

import com.ngleanhvu.dsa_training_system.dto.request.RangeRequest;
import com.ngleanhvu.dsa_training_system.entity.Submission;
import com.ngleanhvu.dsa_training_system.entity.SubmissionStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SubmissionSpecification {

    public static Specification<Submission> hasProblemIdInRange(RangeRequest<Integer> problemIdRange) {
        return (root, query, cb) -> {
            if (problemIdRange == null) return null;

            if (problemIdRange.getFrom() != null && problemIdRange.getTo() != null) {
                return cb.between(root.get("problem").get("problemId"), problemIdRange.getFrom(), problemIdRange.getTo());
            } else if (problemIdRange.getFrom() != null) {
                return cb.greaterThanOrEqualTo(root.get("problem").get("problemId"), problemIdRange.getFrom());
            } else if (problemIdRange.getTo() != null) {
                return cb.lessThanOrEqualTo(root.get("problem").get("problemId"), problemIdRange.getTo());
            } else {
                return null;
            }
        };
    }

    public static Specification<Submission> hasSubmissionStatuses(List<String> statusList) {
        return (root, query, cb) -> {
            if (statusList == null || statusList.isEmpty()) return null;

            List<SubmissionStatus> validStatuses = statusList.stream()
                    .map(SubmissionStatus::getSubmissionStatus)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (validStatuses.isEmpty()) return null;

            return root.get("submissionStatus").in(validStatuses);
        };
    }

    public static Specification<Submission> hasTimeRange(RangeRequest<LocalDate> timeRangeRequest) {
        return (root, query, cb) -> {
            if (timeRangeRequest == null) return null;

            if (timeRangeRequest.getFrom() != null && timeRangeRequest.getTo() != null) {
                return cb.between(
                        root.get("createdAt"),
                        timeRangeRequest.getFrom().atStartOfDay(),
                        timeRangeRequest.getTo().atTime(23, 59, 59)
                );
            } else if (timeRangeRequest.getFrom() != null) {
                return cb.greaterThanOrEqualTo(root.get("createdAt"), timeRangeRequest.getFrom().atStartOfDay());
            } else if (timeRangeRequest.getTo() != null) {
                return cb.lessThanOrEqualTo(root.get("createdAt"), timeRangeRequest.getTo().atTime(23, 59, 59));
            } else {
                return null;
            }
        };
    }

    public static Specification<Submission> hasProgrammingLanguages(List<Integer> programmingLanguageIds) {
        return (root, query, cb) -> {
            if (programmingLanguageIds == null || programmingLanguageIds.isEmpty()) {
                return null;
            }
            return root.get("programmingLanguage").get("programmingLanguageId").in(programmingLanguageIds);
        };
    }

    public static Specification<Submission> statisticByProgrammingLanguageAndUserId(String userId) {
        return (root, query, cb) -> {
            query.multiselect(
                    root.get("programmingLanguage").get("name"),
                    cb.count(root)
            );

            query.groupBy(root.get("programmingLanguage").get("name"));

            Predicate byUser = cb.equal(root.get("user").get("userId"), userId);
            Predicate isAccepted = cb.equal(root.get("submissionStatus"), SubmissionStatus.ACCEPTED);

            return cb.and(byUser, isAccepted);
        };
    }

}
