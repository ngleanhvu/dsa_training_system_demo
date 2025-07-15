package com.ngleanhvu.dsa_training_system.repo;

import com.ngleanhvu.dsa_training_system.entity.ProblemTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProblemTopicRepo extends JpaRepository<ProblemTopic, Integer> {

    @Query("SELECT pt FROM ProblemTopic pt WHERE pt.problem.problemId = :problemId")
    List<ProblemTopic> findByProblemId(@Param("problemId") Integer problemId);
}
