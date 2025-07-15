package com.ngleanhvu.dsa_training_system.repo;

import com.ngleanhvu.dsa_training_system.entity.TestCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TestCaseRepo extends JpaRepository<TestCase, Integer> {

    @Query(value = "SELECT t FROM TestCase t WHERE t.problem.problemId = :problemId")
    List<TestCase> findAllByProblemId(@Param("problemId") Integer problemId);

    @Query("SELECT t FROM TestCase t WHERE t.problem.problemId = :problemId")
    Page<TestCase> findAllByProblemId(@Param("problemId") int problemId, Pageable pageable);

}
