package com.ngleanhvu.dsa_training_system.repo;

import com.ngleanhvu.dsa_training_system.entity.ProblemDetail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProblemDetailRepo extends JpaRepository<ProblemDetail, Integer> {

    @Query(value = "SELECT pd FROM ProblemDetail pd WHERE pd.problem.problemId = :problemId")
    Optional<ProblemDetail> findByProblemId(@Value("problemId") Integer problemId);
}
