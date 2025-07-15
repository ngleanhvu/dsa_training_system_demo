package com.ngleanhvu.dsa_training_system.repo;

import com.ngleanhvu.dsa_training_system.entity.Contest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContestRepo extends JpaRepository<Contest, Integer> {
}
