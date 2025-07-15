package com.ngleanhvu.dsa_training_system.repo.spec;

import com.ngleanhvu.dsa_training_system.dto.request.RangeRequest;
import com.ngleanhvu.dsa_training_system.entity.Submission;
import com.ngleanhvu.dsa_training_system.entity.SubmissionStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class SubmissionSpecification {
    public static Specification<Submission> hasProblemId(Integer problemId) {
        return ((root, query, criteriaBuilder) -> {
           if (problemId == null) {
               return null;
           }

           return criteriaBuilder.equal(root.get("problemId"), problemId);
        });
    }

    public static Specification<Submission> hasSubmissionStatus(String status) {
        return ((root, query, criteriaBuilder) -> {
            SubmissionStatus submissionStatus = SubmissionStatus.getSubmissionStatus(status);

            if (submissionStatus == null) {
                return null;
            }

            return criteriaBuilder.equal(root.get("submissionStatus"), submissionStatus);
        });
    }

    public static Specification<Submission> hasTimeRange(RangeRequest<LocalDate> timeRangeRequest) {
        return (root, query, criteriaBuilder) -> {
            if (timeRangeRequest == null || timeRangeRequest.getFrom() == null || timeRangeRequest.getTo() == null) {
                return null;
            }

            return criteriaBuilder.between(
                    root.get("createdAt"),
                    timeRangeRequest.getFrom().atStartOfDay(),
                    timeRangeRequest.getTo().atTime(23, 59, 59)
            );
        };
    }

    public static Specification<Submission> hasProgrammingLanguage(Integer programmingLanguageId) {
        return ((root, query, criteriaBuilder) -> {
            if (programmingLanguageId == null) {
                return null;
            }

            return criteriaBuilder.equal(root.get("programmingLanguage").get("programmingLanguageId"), programmingLanguageId);
        });
    }

}
