package com.ngleanhvu.dsa_training_system.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "solutions", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"discuss_id", "problem_id"})
})
@Builder @AllArgsConstructor @NoArgsConstructor @Getter
@Setter
public class Solution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer solutionId;

    @OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JoinColumn(name = "discuss_id", nullable = false)
    private Discuss discuss;

    @JoinColumn(name = "problem_id")
    @ManyToOne
    private Problem problem;

    private Integer status = 1;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

