package com.ngleanhvu.dsa_training_system.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "discuss_tags", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"discuss_id","tag_id"})
})
@Builder @NoArgsConstructor @AllArgsConstructor @Getter
@Setter
public class DiscussTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer discussTagId;

    @JoinColumn(name = "discuss_id")
    @ManyToOne
    private Discuss discuss;

    @JoinColumn(name = "tag_id")
    @ManyToOne
    private Tag tag;

    private Integer status = 1;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
