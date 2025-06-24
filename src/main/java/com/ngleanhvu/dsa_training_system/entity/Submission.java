package com.ngleanhvu.dsa_training_system.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "submissions")
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer submissionId;

    @ManyToOne
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @ManyToOne
    @JoinColumn(name = "programming_language_id", nullable = false)
    private ProgrammingLanguage programmingLanguage;

    @Lob
    @Column(nullable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    private SubmissionStatus submissionStatus = SubmissionStatus.Pending;

    private Integer runtimeMs;
    private Integer memoryKb;
    private Integer testCasesPassed = 0;
    private Integer totalTestCases = 0;

    @Lob
    private String errorMessage;

    @Column(nullable = false)
    private LocalDateTime submittedAt = LocalDateTime.now();

    private Integer status = 1;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

