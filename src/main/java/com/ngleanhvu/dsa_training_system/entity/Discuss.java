package com.ngleanhvu.dsa_training_system.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "discuss")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
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

    @Column(name = "comment_count")
    private Integer commentCount = 0;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    private Integer status = 1;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "discuss")
    private Set<DiscussTag> discussTags;

}

