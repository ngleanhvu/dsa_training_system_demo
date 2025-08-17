package com.ngleanhvu.dsa_training_system.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "auth_oauth2", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"provider", "provider_user_id"})
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthOAuth2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private OAuth2Provider provider = OAuth2Provider.GOOGLE;

    @Column(name = "provider_user_id", length = 100, nullable = false)
    private String providerUserId;

    @Column(name = "email", length = 100)
    private String email;

    private int status = 1;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
