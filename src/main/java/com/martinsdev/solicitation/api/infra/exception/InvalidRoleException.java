package com.martinsdev.solicitation.api.infra.exception;

public class InvalidRoleException extends RuntimeException {
    public InvalidRoleException(String message) {
        super("Role must be ANALYST or ADMIN. CLIENT role cannot be assigned by admin");
    }
}
