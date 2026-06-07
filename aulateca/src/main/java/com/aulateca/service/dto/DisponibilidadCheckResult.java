package com.aulateca.service.dto;

/** Resultado de comprobar disponibilidad para una reserva. */
public record DisponibilidadCheckResult(Tipo tipo, String mensaje) {

    public enum Tipo { OK, AVISO, ERROR }

    public static DisponibilidadCheckResult ok(String mensaje) {
        return new DisponibilidadCheckResult(Tipo.OK, mensaje);
    }

    public static DisponibilidadCheckResult aviso(String mensaje) {
        return new DisponibilidadCheckResult(Tipo.AVISO, mensaje);
    }

    public static DisponibilidadCheckResult error(String mensaje) {
        return new DisponibilidadCheckResult(Tipo.ERROR, mensaje);
    }
}
