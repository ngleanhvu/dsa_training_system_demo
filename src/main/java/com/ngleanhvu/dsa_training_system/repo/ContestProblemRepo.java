package com.ngleanhvu.dsa_training_system.repo;

import com.ngleanhvu.dsa_training_system.entity.ContestProblem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ContestProblemRepo extends JpaRepository<ContestProblem, Integer> {

    @Query("SELECT c FROM ContestProblem c WHERE c.contest.contestId = :contestId")
    List<ContestProblem> findByContestId(@Param("contestId") Integer contestId);

    @Transactional
    @Modifying
    @Query("DELETE FROM ContestProblem c WHERE c.contest.contestId = :contestId AND c.problem.problemId IN :problemIds")
    void deleteByContestIdAndProblemId(@Param("contestId") Integer contestId, @Param("problemIds") Set<Integer> problemIds);

    @Query("SELECT cp FROM ContestProblem cp WHERE cp.problem.problemId = :problemId")
    Optional<ContestProblem> findByProblemId(@Param("problemId") Integer problemId);
}
