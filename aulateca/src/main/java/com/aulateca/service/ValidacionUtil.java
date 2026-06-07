package com.aulateca.service;

import com.aulateca.exception.ValidationException;

/** Validaciones reutilizables de datos de entrada. */
public final class ValidacionUtil {

    private ValidacionUtil() {}

    public static void requerirNoVacio(String valor, String mensaje) {
        if (valor == null || valor.isBlank()) {
            throw new ValidationException(mensaje);
        }
    }

    public static void requerirEmail(String email) {
        requerirNoVacio(email, "El email es obligatorio.");
        if (!email.contains("@") || !email.contains(".")) {
            throw new ValidationException("El email no tiene un formato válido.");
        }
    }

    public static void requerirNoNulo(Object valor, String mensaje) {
        if (valor == null) {
            throw new ValidationException(mensaje);
        }
    }
}
