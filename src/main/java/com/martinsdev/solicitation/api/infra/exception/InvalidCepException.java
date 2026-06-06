package com.martinsdev.solicitation.api.infra.exception;

public class InvalidCepException extends RuntimeException {
    public InvalidCepException(String cep) {
        super("This cep (" + cep + ") is invalid!");
    }
}
