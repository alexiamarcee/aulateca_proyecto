package com.aulateca.exception;

/** Violación de una regla de negocio del sistema. */
public class BusinessException extends AulatecaException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
