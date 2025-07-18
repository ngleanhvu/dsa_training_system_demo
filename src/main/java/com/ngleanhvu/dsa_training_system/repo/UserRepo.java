package com.ngleanhvu.dsa_training_system.repo;

import com.ngleanhvu.dsa_training_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u JOIN FETCH u.userDetails WHERE u.email = :email")
    Optional<User> findUserDetailsByEmail(@Param("email") String email);
}
