package com.ngleanhvu.dsa_training_system.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "problem_comments")
@Data
public class ProblemComment {
    @Id
    @Column(name = "comment_id")
    private Integer commentId;

    @MapsId
    @OneToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "problem_id")
    private Problem problem;
}
