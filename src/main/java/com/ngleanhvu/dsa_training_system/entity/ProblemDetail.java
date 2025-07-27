package com.ngleanhvu.dsa_training_system.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "problem_details")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer problemDetailId;

    @Lob
    private String description;

    @Column(columnDefinition = "text")
    private String constraints;

    @Column(columnDefinition = "json")
    private String hints;

    private Integer timeLimit = 1000;

    private Integer memoryLimit = 256;

    @OneToOne
    @JoinColumn(name = "problem_id", unique = true, nullable = false)
    private Problem problem;

    private Integer status = 1;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
