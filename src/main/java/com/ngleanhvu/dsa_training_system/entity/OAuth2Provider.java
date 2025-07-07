package com.ngleanhvu.dsa_training_system.entity;

import lombok.Getter;

@Getter
public enum OAuth2Provider {
    GOOGLE("Google"), GITHUB("Github");

    private final String value;

    OAuth2Provider(String value) {
        this.value = value;
    }
}
