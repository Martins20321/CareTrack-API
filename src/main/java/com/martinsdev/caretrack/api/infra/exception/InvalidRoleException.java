package com.martinsdev.caretrack.api.infra.exception;

public class InvalidRoleException extends RuntimeException {
    public InvalidRoleException() {
        super("Role must be ANALYST or ADMIN. CLIENT role cannot be assigned by admin");
    }
}
