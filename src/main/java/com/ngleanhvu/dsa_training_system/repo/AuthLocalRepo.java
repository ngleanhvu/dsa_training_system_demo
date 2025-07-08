package com.ngleanhvu.dsa_training_system.repo;

import com.ngleanhvu.dsa_training_system.entity.AuthLocal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AuthLocalRepo extends JpaRepository<AuthLocal, String> {

    @Query("SELECT a FROM AuthLocal a WHERE a.email = :email")
    Optional<AuthLocal> findByEmail(@Param("email") String email);
}
