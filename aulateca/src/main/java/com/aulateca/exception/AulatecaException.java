package com.aulateca.exception;

/** Excepción base de la aplicación. */
public class AulatecaException extends RuntimeException {

    public AulatecaException(String message) {
        super(message);
    }

    public AulatecaException(String message, Throwable cause) {
        super(message, cause);
    }
}
