package com.ngleanhvu.dsa_training_system.entity;

import com.ngleanhvu.dsa_training_system.converter.SubmissionStatusConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "submissions")@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @Convert(converter = SubmissionStatusConverter.class)
    @Column(name = "submission_status")
    private SubmissionStatus submissionStatus;

    @Column(name = "runtime_ms")
    private Integer runtimeMs;

    @Column(name = "memory_kb")
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

