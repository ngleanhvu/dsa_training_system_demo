package com.ngleanhvu.dsa_training_system.repo;

import com.ngleanhvu.dsa_training_system.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProblemRepo extends JpaRepository<Problem, Integer>, JpaSpecificationExecutor<Problem> {
    @Query(value = """
    SELECT d.name,
        COUNT(DISTINCT p.problem_id)
    FROM problems p
    JOIN difficulties d ON p.difficulty_id = d.difficulty_id
    LEFT JOIN submissions s ON s.problem_id = p.problem_id
    GROUP BY d.name
""", nativeQuery = true)
    List<Object[]> getDifficultyStats();


    @Query(value = """
    SELECT p.problem_id, p.title, COUNT(s.problem_id)
    FROM problems p
    LEFT JOIN submissions s ON s.problem_id = p.problem_id
    GROUP BY p.problem_id, p.title
    ORDER BY COUNT(s.problem_id) DESC
    LIMIT 5
""", nativeQuery = true)
    List<Object[]> getTop5ProblemsSubmissions();
}
