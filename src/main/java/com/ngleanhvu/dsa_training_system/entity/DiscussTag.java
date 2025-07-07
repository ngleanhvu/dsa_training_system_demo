package com.ngleanhvu.dsa_training_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "discuss_tags", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"discuss_id","tag_id"})
})
@Builder @NoArgsConstructor @AllArgsConstructor @Data
public class DiscussTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer discussTagId;

    @ManyToOne
    private Discuss discuss;

    @ManyToOne
    private Tag tag;

    private Integer status = 1;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
