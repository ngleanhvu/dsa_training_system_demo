package com.ngleanhvu.dsa_training_system.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "contests")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer contestId;

    private String title;

    private String slug;

    @Lob
    private String description;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer durationMinutes = 90;

    private Boolean isRated = true;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('upcoming','ongoing','finished') DEFAULT 'upcoming'")
    private ContestStatus contestStatus = ContestStatus.UPCOMING;

    private Integer status = 1;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
