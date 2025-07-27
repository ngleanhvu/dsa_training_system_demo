package com.ngleanhvu.dsa_training_system.exception;

public class PermissionException extends BaseException {
    public PermissionException(String resourceName, String resourceId) {
        super(String.format("%s với %s không có quyền", resourceName, resourceId));
    }
}
