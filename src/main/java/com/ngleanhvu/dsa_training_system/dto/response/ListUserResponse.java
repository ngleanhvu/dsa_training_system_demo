package com.ngleanhvu.dsa_training_system.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ListUserResponse {
    private List<UserResponse> users;
    private int page;
    private int totalPages;
}
