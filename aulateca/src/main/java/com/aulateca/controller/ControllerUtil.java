package com.aulateca.controller;

import com.aulateca.exception.AulatecaException;

import java.util.Optional;
import java.util.function.Supplier;

/** Utilidades comunes para los controladores MVC. */
public final class ControllerUtil {

    private ControllerUtil() {}

    /** Ejecuta una acción y captura excepciones de la aplicación. */
    public static Optional<String> ejecutar(Runnable accion) {
        try {
            accion.run();
            return Optional.empty();
        } catch (AulatecaException e) {
            return Optional.of(e.getMessage());
        } catch (RuntimeException e) {
            return Optional.of("Error inesperado: " + e.getMessage());
        }
    }

    /** Ejecuta una consulta y devuelve el error si ocurre. */
    public static <T> Resultado<T> ejecutarConsulta(Supplier<T> consulta) {
        try {
            return Resultado.exito(consulta.get());
        } catch (AulatecaException e) {
            return Resultado.error(e.getMessage());
        } catch (RuntimeException e) {
            return Resultado.error("Error inesperado: " + e.getMessage());
        }
    }

    /** Resultado de una operación del controlador. */
    public record Resultado<T>(T datos, String error) {
        public static <T> Resultado<T> exito(T datos) {
            return new Resultado<>(datos, null);
        }
        public static <T> Resultado<T> error(String mensaje) {
            return new Resultado<>(null, mensaje);
        }
        public boolean esError() { return error != null; }
    }
}
