package com.aulateca.exception;

/** Datos de entrada inválidos o incompletos. */
public class ValidationException extends AulatecaException {

    public ValidationException(String message) {
        super(message);
    }
}
