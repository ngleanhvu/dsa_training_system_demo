package com.ngleanhvu.dsa_training_system.entity;

import com.ngleanhvu.dsa_training_system.converter.SubmissionStatusConverter;
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

    @Convert(converter = SubmissionStatusConverter.class)
    @Column(name = "submission_status")
    private SubmissionStatus submissionStatus;

    @Column(name = "runtime")
    private int runtimeMs;

    @Column(name = "memory")
    private int memoryKb;

    private Integer status = 1;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
