package com.ngleanhvu.dsa_training_system.repo;

import com.ngleanhvu.dsa_training_system.entity.Solution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SolutionRepo extends JpaRepository<Solution, Integer> {

    @Query("SELECT COUNT(s) FROM Solution s WHERE s.discuss.user.userId = :userId")
    int countByUserId(@Param("userId") String userId);
}
