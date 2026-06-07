package com.aulateca.service;

import com.aulateca.dao.UserDAO;
import com.aulateca.exception.BusinessException;
import com.aulateca.exception.ValidationException;
import com.aulateca.model.User;

import java.util.List;
import java.util.Optional;

/** Lógica de negocio de usuarios y autenticación. */
public class UserService {

    private final UserDAO userDAO = new UserDAO();

    /** Autentica un usuario activo con email y contraseña. */
    public Optional<User> autenticar(String email, String password) {
        ValidacionUtil.requerirNoVacio(email, "Introduce el correo electrónico.");
        ValidacionUtil.requerirNoVacio(password, "Introduce la contraseña.");
        return userDAO.autenticar(email.trim(), password);
    }

    public List<User> listarTodos() {
        return userDAO.buscarTodos();
    }

    public List<User> listarActivos() {
        return userDAO.buscarActivos();
    }

    /** Crea un nuevo usuario tras validar los datos. */
    public void crear(String nombre, String apellidos, String email,
                      String password, User.Rol rol) {
        validarDatosCompletos(nombre, apellidos, email, password);
        ValidacionUtil.requerirNoNulo(rol, "Debe seleccionar un rol.");

        String emailNorm = email.trim();
        if (userDAO.buscarPorEmail(emailNorm).isPresent()) {
            throw new ValidationException("Ya existe un usuario con ese email.");
        }

        try {
            userDAO.guardar(new User(
                nombre.trim(), apellidos.trim(), emailNorm, password, rol));
        } catch (RuntimeException e) {
            throw new BusinessException("No se pudo crear el usuario.", e);
        }
    }

    /** Actualiza los datos de un usuario existente. */
    public void actualizar(User usuario, String nombre, String apellidos,
                           String email, String password, User.Rol rol, boolean activo) {
        ValidacionUtil.requerirNoNulo(usuario, "Usuario no encontrado.");
        validarDatosCompletos(nombre, apellidos, email, password);
        ValidacionUtil.requerirNoNulo(rol, "Debe seleccionar un rol.");

        usuario.setNombre(nombre.trim());
        usuario.setApellidos(apellidos.trim());
        usuario.setEmail(email.trim());
        usuario.setPassword(password);
        usuario.setRol(rol);
        usuario.setActivo(activo);

        try {
            userDAO.actualizar(usuario);
        } catch (RuntimeException e) {
            throw new BusinessException("No se pudo actualizar el usuario.", e);
        }
    }

    /** Activa o desactiva un usuario. */
    public void cambiarEstadoActivo(User usuario) {
        ValidacionUtil.requerirNoNulo(usuario, "Usuario no encontrado.");
        usuario.setActivo(!usuario.isActivo());
        try {
            userDAO.actualizar(usuario);
        } catch (RuntimeException e) {
            throw new BusinessException("No se pudo cambiar el estado del usuario.", e);
        }
    }

    private void validarDatosCompletos(String nombre, String apellidos,
                                       String email, String password) {
        ValidacionUtil.requerirNoVacio(nombre, "El nombre es obligatorio.");
        ValidacionUtil.requerirNoVacio(apellidos, "Los apellidos son obligatorios.");
        ValidacionUtil.requerirEmail(email);
        ValidacionUtil.requerirNoVacio(password, "La contraseña es obligatoria.");
    }
}
