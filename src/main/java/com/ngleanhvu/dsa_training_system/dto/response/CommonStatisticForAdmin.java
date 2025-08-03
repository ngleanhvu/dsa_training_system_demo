package com.ngleanhvu.dsa_training_system.dto.response;

import lombok.Data;

@Data
public class CommonStatisticForAdmin {
    private int totalUsers;
    private int totalProblems;
    private int totalDiscusses;
    private int totalContests;
}
