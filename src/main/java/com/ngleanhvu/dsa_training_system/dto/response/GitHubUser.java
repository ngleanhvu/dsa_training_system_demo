package com.ngleanhvu.dsa_training_system.dto.response;

import lombok.Data;

@Data
public class GitHubUser {
    private Long id;
    private String login;
    private String name;
    private String email;
    private String avatar_url;
}
