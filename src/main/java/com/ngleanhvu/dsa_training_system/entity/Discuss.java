package com.ngleanhvu.dsa_training_system.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "discuss")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Discuss {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer discussId;

    private String title;

    @Lob
    private String content;

    @Column(name = "views")
    private Integer views = 0;

    @Column(name = "up_votes")
    private Integer upVotes = 0;

    @Column(name = "down_votes")
    private Integer downVotes = 0;

    private Integer status = 1;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}

