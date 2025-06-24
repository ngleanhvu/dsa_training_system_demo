package com.ngleanhvu.dsa_training_system.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "programming_languages")
public class ProgrammingLanguage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer programmingLanguageId;

    @Column(nullable = false, length = 50)
    private String name;

    private String version;

    @Column(length = 10)
    private String fileExtension;

    private Boolean isActive = true;

    private Integer status = 1;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
