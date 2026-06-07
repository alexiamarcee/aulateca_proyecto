package com.aulateca.controller;

import com.aulateca.model.User;
import com.aulateca.service.UserService;

import java.util.List;
import java.util.Optional;

/** Controlador de gestión de usuarios. */
public class UserController {

    private final UserService userService = new UserService();

    public ControllerUtil.Resultado<List<User>> listarUsuarios() {
        return ControllerUtil.ejecutarConsulta(userService::listarTodos);
    }

    public Optional<String> crear(String nombre, String apellidos, String email,
                                  String password, User.Rol rol) {
        return ControllerUtil.ejecutar(() ->
            userService.crear(nombre, apellidos, email, password, rol));
    }

    public Optional<String> actualizar(User usuario, String nombre, String apellidos,
                                       String email, String password, User.Rol rol,
                                       boolean activo) {
        return ControllerUtil.ejecutar(() ->
            userService.actualizar(usuario, nombre, apellidos, email, password, rol, activo));
    }

    public Optional<String> cambiarEstadoActivo(User usuario) {
        return ControllerUtil.ejecutar(() -> userService.cambiarEstadoActivo(usuario));
    }
}
