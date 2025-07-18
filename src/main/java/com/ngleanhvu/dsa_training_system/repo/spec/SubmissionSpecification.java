package com.ngleanhvu.dsa_training_system.repo.spec;

import com.ngleanhvu.dsa_training_system.dto.request.RangeRequest;
import com.ngleanhvu.dsa_training_system.entity.Difficulty;
import com.ngleanhvu.dsa_training_system.entity.Problem;
import com.ngleanhvu.dsa_training_system.entity.Submission;
import com.ngleanhvu.dsa_training_system.entity.SubmissionStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class SubmissionSpecification {

    public static Specification<Submission> hasProblemId(Integer problemId) {
        return (root, query, cb) -> {
            if (problemId == null) {
                return null;
            }
            return cb.equal(root.get("problem").get("problemId"), problemId);
        };
    }

    public static Specification<Submission> hasSubmissionStatus(String status) {
        return (root, query, cb) -> {
            if (status == null || status.trim().isEmpty()) return null;

            SubmissionStatus submissionStatus = SubmissionStatus.getSubmissionStatus(status);
            if (submissionStatus == null) return null;

            return cb.equal(root.get("submissionStatus"), submissionStatus);
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

    public static Specification<Submission> hasProgrammingLanguage(Integer programmingLanguageId) {
        return (root, query, cb) -> {
            if (programmingLanguageId == null) {
                return null;
            }
            return cb.equal(root.get("programmingLanguage").get("programmingLanguageId"), programmingLanguageId);
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
