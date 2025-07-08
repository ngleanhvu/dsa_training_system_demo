package com.ngleanhvu.dsa_training_system.entity;

import com.ngleanhvu.dsa_training_system.converter.UserRoleConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Builder @Data @NoArgsConstructor @AllArgsConstructor
public class User {

    @Id
    @Column(name = "user_id", length = 36)
    private String userId;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "display_name", nullable = false, length = 50)
    private String displayName;

    @Column(length = 255)
    private String avatar;

    @Convert(converter = UserRoleConverter.class)
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;

    private Integer status = 1;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


}
