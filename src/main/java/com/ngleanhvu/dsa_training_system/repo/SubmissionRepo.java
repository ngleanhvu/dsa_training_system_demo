package com.ngleanhvu.dsa_training_system.repo;

import com.ngleanhvu.dsa_training_system.dto.request.ProgrammingStat;
import com.ngleanhvu.dsa_training_system.dto.request.SubmissionCountProjection;
import com.ngleanhvu.dsa_training_system.dto.request.TopicSubmissionStat;
import com.ngleanhvu.dsa_training_system.entity.Submission;
import com.ngleanhvu.dsa_training_system.entity.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubmissionRepo extends JpaRepository<Submission, Integer>, JpaSpecificationExecutor<Submission> {
    @Query("SELECT COUNT(s) AS totalSubmission, " +
            "SUM(CASE WHEN s.submissionStatus = :status THEN 1 ELSE 0 END) AS totalAccepted " +
            "FROM Submission s WHERE s.problem.problemId = :problemId")
    SubmissionCountProjection countSubmissionByProblemAndStatus(@Param("status") SubmissionStatus status,
                                                                @Param("problemId") Integer problemId);

    @Query("SELECT s FROM Submission s WHERE s.user.userId = :userId AND s.problem.problemId = :problemId")
    List<Submission> getSubmissionByUserIdAndProblemId(@Param("userId") String userId,
                                                       @Param("problemId") int problemId);


    @Query(value = """
    SELECT p.name AS programming_language_name,
        COUNT(*) AS total_submission,
        COUNT(IF(s.submission_status = 'Accepted' AND s.user_id = :userId, 1, NULL)) AS total_accepted
    FROM submissions s
    JOIN programming_languages p ON s.programming_language_id = p.programming_language_id
    WHERE s.user_id = :userId
    GROUP BY p.name
""", nativeQuery = true)
    List<ProgrammingStat> statisticSubmissionByProgrammingLanguageAndUserId(@Param("userId") String userId);


    @Query(value = """
    SELECT t.name AS topic_name,
        COUNT(DISTINCT p.problem_id) AS total_problems,
        COUNT(DISTINCT CASE WHEN s.submission_status = 'Accepted' AND s.user_id = :userId THEN p.problem_id END) AS accepted_problems
    FROM problems p
    LEFT JOIN problems_topics pt ON pt.problem_id = p.problem_id
    LEFT JOIN topics t ON t.topic_id = pt.topic_id
    LEFT JOIN submissions s ON s.problem_id = p.problem_id
    GROUP BY t.name
""", nativeQuery = true)
    List<TopicSubmissionStat> statisticSubmissionByTopicIdAndUserId(@Param("userId") String userId);

}
