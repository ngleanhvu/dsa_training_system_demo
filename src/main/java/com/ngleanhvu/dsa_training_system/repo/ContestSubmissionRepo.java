package com.ngleanhvu.dsa_training_system.repo;

import com.ngleanhvu.dsa_training_system.entity.ContestSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContestSubmissionRepo extends JpaRepository<ContestSubmission, Integer> {
}
