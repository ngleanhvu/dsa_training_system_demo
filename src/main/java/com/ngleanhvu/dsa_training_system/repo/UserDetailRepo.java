package com.ngleanhvu.dsa_training_system.repo;

import com.ngleanhvu.dsa_training_system.entity.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDetailRepo extends JpaRepository<UserDetails, String> {
}
