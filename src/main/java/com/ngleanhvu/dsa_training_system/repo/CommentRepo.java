package com.ngleanhvu.dsa_training_system.repo;

import com.ngleanhvu.dsa_training_system.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentRepo extends JpaRepository<Comment, Integer> {

    @Query("""
    SELECT c 
    FROM Comment c 
    WHERE c.discuss.discussId = :discussId 
      AND c.parent IS NULL
""")
    Page<Comment> findCommentsByDiscuss(@Param("discussId") int discussId, Pageable pageable);


    @Query(value = "SELECT c FROM Comment c WHERE c.parent.commentId = :parentCommentId")
    Page<Comment> findCommentsByParentComment(@Param("parentCommentId") int parentCommentId, Pageable pageable);



    @Query(value = """
    SELECT 
        c.comment_id,
        c.content,
        c.comment_count,
        c.up_votes,
        c.created_at,
        u.email,
        u.display_name,
        u.avatar,
        CAST(CASE WHEN cv.user_id IS NOT NULL THEN 1 ELSE 0 END AS UNSIGNED) AS is_voted
    FROM comments c
    LEFT JOIN comments_votes cv 
        ON cv.comment_id = c.comment_id 
        AND cv.user_id = :userId
    LEFT JOIN users u 
        ON u.user_id = c.user_id
    WHERE c.parent_id = :parentCommentId
""",
            countQuery = """
    SELECT COUNT(*)
    FROM comments c
    WHERE c.parent_id = :parentCommentId
""",
            nativeQuery = true)
    Page<Object[]> findCommentsByParentCommentWithUser(
            @Param("parentCommentId") int parentCommentId,
            @Param("userId") String userId,
            Pageable pageable);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.user.email = :email")
    int countByUserEmail(@Param("email") String email);

    @Query("""
    SELECT c
    FROM Comment c
    JOIN ProblemComment pc ON pc.comment.commentId = c.commentId
    WHERE pc.problem.problemId = :problemId
""")
    Page<Comment> findCommentByProblem(@Param("problemId") int problemId, Pageable pageable);

    @Query(value = """
    SELECT c.comment_id,
        c.content,
        c.comment_count,
        c.up_votes,
        c.created_at,
        u.email,
        u.display_name,
        u.avatar,
        CAST(CASE WHEN cv.user_id IS NOT NULL THEN 1 ELSE 0 END AS UNSIGNED) AS is_voted
    FROM comments c
    LEFT JOIN problem_comments pc 
        ON pc.comment_id = c.comment_id
    LEFT JOIN comments_votes cv 
        ON cv.comment_id = c.comment_id AND cv.user_id = :userId
    LEFT JOIN users u ON u.user_id = c.user_id
    WHERE pc.problem_id = :problemId
""",
            countQuery = """
    SELECT COUNT(*)
    FROM comments c
    LEFT JOIN problem_comments pc 
        ON pc.comment_id = c.comment_id
    WHERE pc.problem_id = :problemId
""",
            nativeQuery = true)
    Page<Object[]> findCommentByUser(@Param("userId") String userId,
                                     @Param("problemId") int problemId,
                                     Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.commentId = :commentId")
    Optional<Comment> findCommentById(@Param("commentId") Integer commentId);

    @Query(value = """
    SELECT c.comment_id,
           c.content,
           c.comment_count,
           c.up_votes,
           c.created_at,
           u.email,
           u.display_name,
           u.avatar,
           CAST(CASE WHEN cv.user_id IS NOT NULL THEN 1 ELSE 0 END AS UNSIGNED) AS is_voted
    FROM comments c 
    LEFT JOIN comments_votes cv 
           ON cv.comment_id = c.comment_id 
           AND cv.user_id = :userId
    LEFT JOIN users u 
           ON u.user_id = c.user_id
    WHERE c.discuss_id = :discussId AND c.parent_id IS NULL
""",
            countQuery = """
    SELECT COUNT(*)
    FROM comments c
    WHERE c.discuss_id = :discussId AND c.parent_id IS NULL
""",
            nativeQuery = true)
    Page<Object[]> findCommentByDiscussWithCredential(@Param("discussId") Integer discussId,
                                                      @Param("userId") String userId,
                                                      Pageable pageable);

}

