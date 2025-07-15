package com.ngleanhvu.dsa_training_system.repo;

import com.ngleanhvu.dsa_training_system.entity.ContestParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContestParticipantRepo extends JpaRepository<ContestParticipant, Integer> {
}
