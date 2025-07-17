package com.ngleanhvu.dsa_training_system.repo;

import com.ngleanhvu.dsa_training_system.entity.DiscussTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

public interface DiscussTagRepo extends JpaRepository<DiscussTag, Integer> {

    @Query("SELECT dt FROM DiscussTag dt WHERE dt.discuss.discussId = :discussId")
    List<DiscussTag> findByDiscussId(@Param("discussId") int discussId);

    @Transactional
    @Modifying
    @Query("DELETE FROM DiscussTag dt WHERE dt.discuss.discussId = :discussId AND dt.tag.tagId IN :tagIds")
    void deleteByDiscussIdAndTagIds(@Param("discussId") Integer discussId, @Param("tagIds") Set<Integer> tagIds);

}
