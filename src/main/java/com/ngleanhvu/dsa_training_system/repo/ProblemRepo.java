package com.ngleanhvu.dsa_training_system.repo;

import com.ngleanhvu.dsa_training_system.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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


    @Query(value = """
    SELECT d.difficulty_id,
           d.name AS difficulty_name,
           COUNT(DISTINCT CASE WHEN s.user_id = (
               SELECT u.user_id FROM users u WHERE u.email = :email
           ) THEN p.problem_id END) AS solved_count,
           COUNT(DISTINCT p.problem_id) AS total_count
    FROM problems p
    JOIN difficulties d ON d.difficulty_id = p.difficulty_id
    LEFT JOIN submissions s ON s.problem_id = p.problem_id
    GROUP BY d.difficulty_id, d.name
""", nativeQuery = true)
    List<Object[]> statsProblemByDifficultAndUserEmail(@Param("email") String email);


    @Query(value = """
    SELECT 
        (SELECT COUNT(DISTINCT s.problem_id)
         FROM submissions s
         JOIN users u ON u.user_id = s.user_id
         WHERE u.email = :email) AS solved_count,
        (SELECT COUNT(DISTINCT p.problem_id) FROM problems p) AS total_count
""", nativeQuery = true)
    List<Object[]> statsProblemSolvedByUserEmail(@Param("email") String email);

}
