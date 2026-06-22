package com.martinsdev.caretrack.api.infra.exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
        super("Access denied: you are not the owner of this resource");
    }
}
