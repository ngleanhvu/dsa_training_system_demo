package com.ngleanhvu.dsa_training_system.converter;

import com.ngleanhvu.dsa_training_system.entity.SubmissionStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class SubmissionStatusConverter implements AttributeConverter<SubmissionStatus, String> {

    @Override
    public String convertToDatabaseColumn(SubmissionStatus status) {
        return status != null ? status.getValue() : null;
    }

    @Override
    public SubmissionStatus convertToEntityAttribute(String value) {
        if (value == null) return null;
        for (SubmissionStatus status : SubmissionStatus.values()) {
            if (status.getValue().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown SubmissionStatus: " + value);
    }
}