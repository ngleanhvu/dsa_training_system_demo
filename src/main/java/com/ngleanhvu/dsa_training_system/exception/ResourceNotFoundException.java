package com.ngleanhvu.dsa_training_system.exception;

public class ResourceNotFoundException extends BaseException {
    private String resource;
    private String field;
    private String value;
    public ResourceNotFoundException(String resource, String field, String value) {
        super(String.format("%s not found with %s: %s", resource, field, value));
    }
}
