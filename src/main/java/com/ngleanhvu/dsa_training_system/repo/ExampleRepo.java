package com.ngleanhvu.dsa_training_system.repo;

import com.ngleanhvu.dsa_training_system.entity.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExampleRepo extends JpaRepository<Example, Integer> {

    @Query("SELECT e FROM Example e WHERE e.problem.problemId = :problemId")
    List<Example> findByProblem(@Param("problemId") Integer problemId);
}
