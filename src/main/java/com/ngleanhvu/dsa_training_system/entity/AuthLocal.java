package com.ngleanhvu.dsa_training_system.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "auth_local")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthLocal {

    @Id
    @Column(name = "user_id", length = 36)
    private String userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "email", length = 50, nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", length = 255, nullable = false)
    private String passwordHash;


    private boolean isVerify = false;

    private int status = 1;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

