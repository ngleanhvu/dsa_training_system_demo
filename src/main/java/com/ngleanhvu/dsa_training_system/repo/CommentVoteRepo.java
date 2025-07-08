package com.ngleanhvu.dsa_training_system.repo;

import com.ngleanhvu.dsa_training_system.entity.CommentVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentVoteRepo extends JpaRepository<CommentVote, Integer> {

    @Query("SELECT cv FROM CommentVote cv WHERE cv.comment.commentId = :commentId")
    Optional<CommentVote> findByCommentId(@Param("commentId") Integer commentId);
}
