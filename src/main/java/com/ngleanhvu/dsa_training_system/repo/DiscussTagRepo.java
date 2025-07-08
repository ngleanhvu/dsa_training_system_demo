package com.ngleanhvu.dsa_training_system.repo;

import com.ngleanhvu.dsa_training_system.entity.DiscussTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DiscussTagRepo extends JpaRepository<DiscussTag, Integer> {

    @Query("SELECT dt FROM DiscussTag dt WHERE dt.discuss.discussId = :discussId")
    List<DiscussTag> findByDiscussId(@Param("discussId") int discussId);
}
