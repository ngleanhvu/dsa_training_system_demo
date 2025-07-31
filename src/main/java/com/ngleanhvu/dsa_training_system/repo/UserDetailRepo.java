package com.ngleanhvu.dsa_training_system.repo;

import com.ngleanhvu.dsa_training_system.entity.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserDetailRepo extends JpaRepository<UserDetails, String> {
    @Query("SELECT ud FROM UserDetails ud WHERE ud.user.email = :email")
    Optional<UserDetails> findByEmail(@Param("email") String email);
}
