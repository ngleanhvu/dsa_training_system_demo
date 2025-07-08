package com.ngleanhvu.dsa_training_system.converter;

import com.ngleanhvu.dsa_training_system.entity.UserRole;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UserRoleConverter implements AttributeConverter<UserRole, String> {
    @Override
    public String convertToDatabaseColumn(UserRole userRole) {
        return userRole != null ? userRole.getValue(): null;
    }

    @Override
    public UserRole convertToEntityAttribute(String value) {
        if (value == null) return null;
        for (UserRole userRole : UserRole.values()) {
            if (userRole.getValue().equalsIgnoreCase(value)) {
                return userRole;
            }
        }
        throw new IllegalArgumentException("Unknown user role: " + value);
    }
}
