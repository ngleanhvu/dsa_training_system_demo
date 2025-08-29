package com.ngleanhvu.dsa_training_system.repo;

import com.ngleanhvu.dsa_training_system.entity.Discuss;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DiscussRepo extends JpaRepository<Discuss, Integer>, JpaSpecificationExecutor<Discuss> {

    @Query(value = """
    SELECT d.discuss_id,
           d.title,
           d.content,
           d.created_at,
           d.up_votes,
           d.comment_count,
           u.email,
           u.display_name,
           u.avatar,
           CASE WHEN dv.user_id IS NULL THEN 0 ELSE 1 END AS is_up_vote
    FROM discuss d
    JOIN users u ON u.user_id = d.user_id
    LEFT JOIN discusses_votes dv 
           ON dv.discuss_id = d.discuss_id 
          AND dv.user_id = :userId
    WHERE d.title LIKE CONCAT('%', :keyword, '%')
    """,
            countQuery = """
    SELECT COUNT(*) 
    FROM discuss d
    WHERE d.title LIKE CONCAT('%', :keyword, '%')
    """,
            nativeQuery = true)
    Page<Object[]> findDiscussesWithUser(@Param("keyword") String keyword,
                                         @Param("userId") String userId,
                                         Pageable pageable);



    @Query("SELECT COUNT(d) FROM Discuss d WHERE d.user.email= :email")
    int countByUserEmail(@Param("email") String email);

    @Query(value = """
    SELECT d.discuss_id,
           d.title,
           d.content,
           d.created_at,
           d.comment_count,
           d.up_votes,
           u.email,
           u.display_name,
           u.avatar,
           CASE WHEN dv.user_id IS NULL THEN 0 ELSE 1 END AS is_up_vote
    FROM discuss d
    LEFT JOIN discusses_votes dv 
           ON dv.discuss_id = d.discuss_id 
          AND dv.user_id = :userId
    JOIN users u ON u.user_id = d.user_id
    WHERE d.discuss_id = :discussId
    """, nativeQuery = true)
    List<Object[]> findByDiscussWithCredential(@Param("discussId") Integer discussId,
                                               @Param("userId") String userId);


}
