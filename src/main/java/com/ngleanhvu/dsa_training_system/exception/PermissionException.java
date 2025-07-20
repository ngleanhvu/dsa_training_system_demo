package com.ngleanhvu.dsa_training_system.exception;

public class PermissionException extends BaseException {

    private String resourceName;
    private String resourceId;

    public PermissionException(String resourceName, String resourceId) {
        super(String.valueOf("%s with %s doesn't have permission"));
    }
}
