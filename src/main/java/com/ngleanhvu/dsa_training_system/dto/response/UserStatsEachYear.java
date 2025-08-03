package com.ngleanhvu.dsa_training_system.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserStatsEachYear {
    private int month;
    private int quantity;
}
