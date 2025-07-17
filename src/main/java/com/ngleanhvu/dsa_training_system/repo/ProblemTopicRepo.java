package com.ngleanhvu.dsa_training_system.repo;

import com.ngleanhvu.dsa_training_system.entity.ProblemTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

public interface ProblemTopicRepo extends JpaRepository<ProblemTopic, Integer> {

    @Query("SELECT pt FROM ProblemTopic pt WHERE pt.problem.problemId = :problemId")
    List<ProblemTopic> findByProblemId(@Param("problemId") Integer problemId);

    @Transactional
    @Modifying
    @Query("DELETE FROM ProblemTopic pt WHERE pt.problem.problemId = :problemId AND pt.topic.topicId IN :topicIds")
    void deleteByProblemIdAndTopicIds(@Param("problemId") Integer problemId, @Param("topicIds") Set<Integer> topicIds);

}
