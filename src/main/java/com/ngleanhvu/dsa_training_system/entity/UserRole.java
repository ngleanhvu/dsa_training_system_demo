package com.ngleanhvu.dsa_training_system.entity;

import lombok.Getter;

@Getter
public enum UserRole {
    USER("user"), ADMIN("admin");

    private final String value;

    private UserRole(String value) {
        this.value = value;
    }
}
