package com.ngleanhvu.dsa_training_system.repo;

import com.ngleanhvu.dsa_training_system.entity.DiscussVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DiscussVoteRepo extends JpaRepository<DiscussVote, Integer> {

    @Query("SELECT dv FROM DiscussVote dv WHERE dv.discuss.discussId = :discussId")
    Optional<DiscussVote> findByDiscussId(@Param("discussId") int discussId);
}
