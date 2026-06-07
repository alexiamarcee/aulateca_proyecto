package com.aulateca.controller;

import com.aulateca.exception.AulatecaException;
import com.aulateca.model.User;
import com.aulateca.service.UserService;

import java.util.Optional;

/** Controlador de autenticación e inicio de sesión. */
public class LoginController {

    private final UserService userService = new UserService();

    /** Resultado del intento de autenticación. */
    public record AuthResult(User usuario, String error) {}

    /** Intenta autenticar al usuario con email y contraseña (verificación BCrypt en servicio). */
    public AuthResult iniciarSesion(String email, String password) {
        try {
            Optional<User> usuario = userService.autenticar(email, password);
            if (usuario.isPresent()) {
                return new AuthResult(usuario.get(), null);
            }
            return new AuthResult(null, "Correo o contraseña incorrectos.");
        } catch (AulatecaException e) {
            return new AuthResult(null, e.getMessage());
        }
    }

    /** Registra un usuario nuevo con rol ALUMNO e inicia sesión automáticamente. */
    public AuthResult registrar(String nombre, String apellidos, String email,
                                String password, String confirmarPassword) {
        try {
            if (!password.equals(confirmarPassword)) {
                return new AuthResult(null, "Las contraseñas no coinciden.");
            }
            userService.crear(nombre, apellidos, email, password, User.Rol.ALUMNO);
            return iniciarSesion(email, password);
        } catch (AulatecaException e) {
            return new AuthResult(null, e.getMessage());
        }
    }
}
