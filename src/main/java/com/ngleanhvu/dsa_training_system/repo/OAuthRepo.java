package com.ngleanhvu.dsa_training_system.repo;

import com.ngleanhvu.dsa_training_system.entity.AuthOAuth2;
import com.ngleanhvu.dsa_training_system.entity.OAuth2Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OAuthRepo extends JpaRepository<AuthOAuth2, Integer> {

    @Query("SELECT o FROM AuthOAuth2 o WHERE o.email = :email AND o.provider = :provider")
    Optional<AuthOAuth2> findByEmailAndOAuth2Provider(@Param("email") String email,
                                                      @Param("provider") OAuth2Provider provider);
}
