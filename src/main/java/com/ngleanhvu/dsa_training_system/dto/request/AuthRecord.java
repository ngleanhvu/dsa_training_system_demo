package com.ngleanhvu.dsa_training_system.dto.request;

import com.ngleanhvu.dsa_training_system.entity.UserRole;

public record AuthRecord(String userid, UserRole userRole) {
}
