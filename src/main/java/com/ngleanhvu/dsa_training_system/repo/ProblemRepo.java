package com.ngleanhvu.dsa_training_system.repo;

import com.ngleanhvu.dsa_training_system.dto.request.DifficultyStat;
import com.ngleanhvu.dsa_training_system.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProblemRepo extends JpaRepository<Problem, Integer>, JpaSpecificationExecutor<Problem> {
    @Query(value = """
    SELECT d.name AS difficulty_name,
        COUNT(DISTINCT p.problem_id) AS total_problems,
        COUNT(DISTINCT CASE WHEN s.submission_status = 'Accepted' THEN p.problem_id END) AS accepted_problems
    FROM problems p
    JOIN difficulties d ON p.difficulty_id = d.difficulty_id
    LEFT JOIN submissions s ON s.problem_id = p.problem_id
    GROUP BY d.name
""", nativeQuery = true)
    List<DifficultyStat> getDifficultyStats();
}
