package com.ngleanhvu.dsa_training_system.repo;

import com.ngleanhvu.dsa_training_system.dto.request.SubmissionCountProjection;
import com.ngleanhvu.dsa_training_system.entity.Submission;
import com.ngleanhvu.dsa_training_system.entity.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubmissionRepo extends JpaRepository<Submission, Integer> {
    @Query("SELECT COUNT(s) AS totalSubmission, " +
            "SUM(CASE WHEN s.submissionStatus = :status THEN 1 ELSE 0 END) AS totalAccepted " +
            "FROM Submission s WHERE s.problem.problemId = :problemId")
    SubmissionCountProjection countSubmissionByProblemAndStatus(@Param("status") SubmissionStatus status,
                                                                @Param("problemId") Integer problemId);


}
