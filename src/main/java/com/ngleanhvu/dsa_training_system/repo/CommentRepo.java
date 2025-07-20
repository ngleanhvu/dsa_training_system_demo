package com.ngleanhvu.dsa_training_system.repo;

import com.ngleanhvu.dsa_training_system.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepo extends JpaRepository<Comment, Integer> {

    @Query("SELECT c FROM Comment c WHERE c.discuss.discussId = :discussId AND c.parent.commentId IS NULL")
    Page<Comment> findCommentsByDiscuss(@Param("discussId") int discussId, Pageable pageable);

    @Query(value = "SELECT c FROM Comment c WHERE c.parent.commentId = :parentCommentId")
    Page<Comment> findCommentsByParentComment(@Param("parentCommentId") int parentCommentId, Pageable pageable);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.user.email = :email")
    int countByUserEmail(@Param("email") String email);
}
