package com.martinsdev.solicitation.api.infra.exception;

public class InvalidOperationException extends RuntimeException {
    public InvalidOperationException() {
        super("This operation can only be performed if the user is an ANALYST");
    }
}
