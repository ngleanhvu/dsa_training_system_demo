package com.ngleanhvu.dsa_training_system.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DifficultUserResponse {
    private Integer difficultId;
    private String difficultName;
    private Long solved;
    private Long total;
}
