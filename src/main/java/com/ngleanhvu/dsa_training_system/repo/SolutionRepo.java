package com.ngleanhvu.dsa_training_system.repo;

import com.ngleanhvu.dsa_training_system.entity.Solution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolutionRepo extends JpaRepository<Solution, Integer> {
}
