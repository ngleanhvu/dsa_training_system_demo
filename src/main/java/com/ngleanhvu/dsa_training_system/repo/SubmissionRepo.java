package com.ngleanhvu.dsa_training_system.repo;

import com.ngleanhvu.dsa_training_system.dto.request.ProgrammingStat;
import com.ngleanhvu.dsa_training_system.dto.request.SubmissionCountProjection;
import com.ngleanhvu.dsa_training_system.dto.request.TopicSubmissionStat;
import com.ngleanhvu.dsa_training_system.entity.Submission;
import com.ngleanhvu.dsa_training_system.entity.SubmissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.method.P;

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

    @Query("SELECT s FROM Submission s WHERE s.submissionStatus = 'ACCEPTED' AND s.user.userId = :userId AND s.problem.problemId IN :problemIds")
    List<Submission> getSubmissionByUserIdAndProblemStatus(@Param("userId") String userId,
                                                           @Param("problemIds") List<Integer> problemIds);


    @Query(value = """
    SELECT p.name AS programming_language_name,
        COUNT(*) AS total_submission,
        COUNT(IF(s.submission_status = 'Accepted' AND u.user_id = :userId, 1, NULL)) AS total_accepted
    FROM submissions s
    JOIN programming_languages p ON s.programming_language_id = p.programming_language_id
    JOIN users u ON s.user_id = u.user_id
    WHERE u.user_id = :userId
    GROUP BY p.name
""", nativeQuery = true)
    List<ProgrammingStat> statisticSubmissionByProgrammingLanguageAndUserEmail(@Param("userId") String userId);


    @Query(value = """
    SELECT t.name AS topic_name,
        COUNT(DISTINCT p.problem_id) AS total_problems,
        COUNT(DISTINCT CASE WHEN s.submission_status = 'Accepted' AND u.email = :email THEN p.problem_id END) AS accepted_problems
    FROM problems p
    LEFT JOIN problems_topics pt ON pt.problem_id = p.problem_id
    LEFT JOIN topics t ON t.topic_id = pt.topic_id
    LEFT JOIN submissions s ON s.problem_id = p.problem_id
    LEFT JOIN users u ON s.user_id = u.user_id
    GROUP BY t.name
""", nativeQuery = true)
    List<TopicSubmissionStat> statisticSubmissionByTopicIdAndUserEmail(@Param("email") String email);


    @Query(value = """
    SELECT MONTH(s.created_at) AS month, COUNT(*) AS quantity
    FROM submissions s
    WHERE YEAR(s.created_at) = :year
    GROUP BY MONTH(s.created_at)
    ORDER BY MONTH(s.created_at)
""", nativeQuery = true)
    List<Object[]> getSubmissionByEachYear(@Param("year") int year);


    @Query("SELECT s FROM Submission s WHERE s.user.userId = :userId AND s.problem.problemId = :problemId")
    Page<Submission> getSubmissionByUserIdAndProblemId(@Param("userId") String userId,
                                                       @Param("problemId") Integer problemId,
                                                       Pageable pageable);



    @Query(value = """
    SELECT DATE(s.submitted_at) AS submitted_date,
           COUNT(s.submission_id) AS submission_count
    FROM submissions s
    JOIN users u ON s.user_id = u.user_id
    WHERE u.email = :email 
      AND YEAR(s.submitted_at) = :year
    GROUP BY DATE(s.submitted_at)
    ORDER BY submitted_date
""", nativeQuery = true)
    List<Object[]> statsSubmissionByUserEmail(@Param("email") String email,
                                              @Param("year") int year);


}
