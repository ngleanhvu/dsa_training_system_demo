package com.ngleanhvu.dsa_training_system.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "submissions_test_cases")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionTestCase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JoinColumn(name = "submission_id")
    @ManyToOne
    private Submission submission;

    @JoinColumn(name = "test_case_id")
    @ManyToOne
    private TestCase testCase;

    @Column(name = "submission_status")
    @Enumerated(EnumType.STRING)
    private SubmissionStatus submissionStatus = SubmissionStatus.PENDING;

    @Column(name = "runtime")
    private Integer runtimeMs;

    @Column(name = "memory")
    private Integer memoryKb;

    private Integer status = 1;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
