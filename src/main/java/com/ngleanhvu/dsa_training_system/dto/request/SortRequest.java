package com.ngleanhvu.dsa_training_system.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SortRequest {
    private String field;
    private String sortDirection;
}
