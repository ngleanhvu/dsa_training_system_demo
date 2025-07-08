package com.ngleanhvu.dsa_training_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "discusses_votes")
@Builder @AllArgsConstructor @NoArgsConstructor @Data
public class DiscussVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "discuss_vote_id")
    private Integer discussVoteId;

    @JoinColumn(name = "discuss_id")
    @ManyToOne
    private Discuss discuss;

    @JoinColumn(name = "user_id")
    @ManyToOne
    private User user;

    private Integer status = 1;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
