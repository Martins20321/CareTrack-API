package com.martinsdev.caretrack.api.infra.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("This email already exists: " + email );
    }
}
