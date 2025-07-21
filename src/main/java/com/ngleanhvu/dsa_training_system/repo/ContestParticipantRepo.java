package com.ngleanhvu.dsa_training_system.repo;

import com.ngleanhvu.dsa_training_system.entity.ContestParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface ContestParticipantRepo extends JpaRepository<ContestParticipant, Integer> {
    @Query("SELECT c FROM ContestParticipant c WHERE c.user.userId = :userId AND c.contest.contestId = :contestId")
    Optional<ContestParticipant> findByUserIdAndContestId(@Param("userId") String userId,
                                                          @Param("contestId") Integer contestId);

    @Query(value = """
        SELECT\s
              u.display_name,
              u.email,
              cb.problem_id,
              -- solved ?
              CASE\s
                  WHEN COUNT(CASE WHEN cs.is_accepted = true THEN 1 END) > 0 THEN true
                  ELSE false
              END AS solved,
              -- count submission wrong\s
              COUNT(CASE\s
                  WHEN cs.is_accepted = false\s
                       AND (SELECT COUNT(*)\s
                            FROM contest_submissions cs2
                            JOIN submissions s2 ON cs2.submission_id = s2.submission_id
                            WHERE s2.user_id = s.user_id
                              AND s2.problem_id = s.problem_id
                              AND cs2.contest_id = cs.contest_id
                              AND cs2.is_accepted = true\s
                              AND cs2.submitted_at < cs.submitted_at
                           ) = 0
                  THEN 1
                  ELSE null
              END) AS wrong_count,
        
              MIN(CASE WHEN cs.is_accepted = true THEN cs.submitted_at ELSE NULL END) AS solved_at
        
          FROM contest_problems cb
          JOIN contest_participants cp ON cb.contest_id = cp.contest_id
          JOIN users u ON cp.user_id = u.user_id
          LEFT JOIN contest_submissions cs\s
                  ON cs.contest_id = cb.contest_id
          LEFT JOIN submissions s\s
                  ON s.submission_id = cs.submission_id\s
                  AND s.problem_id = cb.problem_id
                  AND s.user_id = cp.user_id
          WHERE cb.contest_id = :contestId
          GROUP BY u.user_id, u.email, cb.problem_id
                                          
""", nativeQuery = true)
    List<Object[]> getRawContestParticipantResults(@Param("contestId") Integer contestId);

}
