package com.ngleanhvu.dsa_training_system.repo;

import com.ngleanhvu.dsa_training_system.entity.Example;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExampleRepo extends JpaRepository<Example, Integer> {
}
