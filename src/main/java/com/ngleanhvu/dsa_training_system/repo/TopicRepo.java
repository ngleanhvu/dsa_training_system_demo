package com.ngleanhvu.dsa_training_system.repo;

import com.ngleanhvu.dsa_training_system.entity.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TopicRepo extends JpaRepository<Topic, Integer> {
    @Query("SELECT t FROM Topic t WHERE t.name LIKE CONCAT('%',:keyword,'%')")
    Page<Topic> findByKeyword(@Param("keyword") String keyword, Pageable pageable);


    @Query(value = """
    SELECT t.name, COUNT(DISTINCT pt.problem_id) AS problem_count
    FROM topics t
    LEFT JOIN problems_topics pt ON t.topic_id = pt.topic_id
    GROUP BY t.topic_id, t.name
""", nativeQuery = true)
    List<Object[]> statsTopic();
}
