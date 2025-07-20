package com.ngleanhvu.dsa_training_system.entity;

import com.ngleanhvu.dsa_training_system.converter.OAuth2ProviderConverter;
import jakarta.persistence.*;
import lombok.*;
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

    @Convert(converter = OAuth2ProviderConverter.class)
    @Enumerated(EnumType.STRING)
    private OAuth2Provider provider = OAuth2Provider.GOOGLE;

    @Column(name = "provider_user_id", length = 100, nullable = false)
    private String providerUserId;

    @Column(name = "email", length = 100)
    private String email;

    private int status = 1;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
