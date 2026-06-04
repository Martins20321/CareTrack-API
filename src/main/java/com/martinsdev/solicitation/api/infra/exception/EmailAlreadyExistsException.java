package com.martinsdev.solicitation.api.infra.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("This email already exists: " + email );
    }
}
