package com.ngleanhvu.dsa_training_system.converter;

import com.ngleanhvu.dsa_training_system.entity.OAuth2Provider;
import com.ngleanhvu.dsa_training_system.entity.SubmissionStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class OAuth2ProviderConverter implements AttributeConverter<OAuth2Provider, String> {

    @Override
    public String convertToDatabaseColumn(OAuth2Provider provider) {
        return provider != null ? provider.getValue() : null;
    }

    @Override
    public OAuth2Provider convertToEntityAttribute(String value) {
        if (value == null) return null;
        for (OAuth2Provider provider : OAuth2Provider.values()) {
            if (provider.getValue().equalsIgnoreCase(value)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("Unknown provier: " + value);
    }

}
