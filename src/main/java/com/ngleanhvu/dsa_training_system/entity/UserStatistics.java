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
@Table(name = "user_statistics")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class UserStatistics {

    @Id
    @Column(name = "user_id", length = 36)
    private String userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "ranking_score")
    private Integer rankingScore = 0;

    private Integer views = 0;

    private Integer solutions = 0;

    @Column(name = "acceptance_rate")
    private Double acceptanceRate = 0.0;

    private Integer reputation = 0;

    private Integer submission = 0;

    private Integer discuss = 0;

    @Column(columnDefinition = "json")
    private String languages;

    private Integer status = 1;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}

