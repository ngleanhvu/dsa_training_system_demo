package com.ngleanhvu.dsa_training_system.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "problems")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer problemId;

    private String title;

    @Column(length = 200)
    private String slug;

    @ManyToOne
    @JoinColumn(name = "difficulty_id", nullable = false)
    private Difficulty difficulty;

    @Column(precision = 5, scale = 2)
    private BigDecimal acceptanceRate = BigDecimal.ZERO;

    private Integer status = 1;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
