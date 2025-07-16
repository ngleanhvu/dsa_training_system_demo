package com.ngleanhvu.dsa_training_system.repo;

import com.ngleanhvu.dsa_training_system.entity.Discuss;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface DiscussRepo extends JpaRepository<Discuss, Integer>, JpaSpecificationExecutor<Discuss> {

    @Query(value = "SELECT d FROM Discuss d WHERE d.content LIKE concat('%',:keyword,'%')")
    Page<Discuss> findDiscusses(@Param("keyword") String keyword, Pageable pageable);
}
