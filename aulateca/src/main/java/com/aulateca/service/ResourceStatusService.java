package com.aulateca.service;

import com.aulateca.dao.ResourceStatusDAO;
import com.aulateca.exception.BusinessException;
import com.aulateca.model.ResourceStatus;

import java.util.List;

/** Lógica de negocio de estados de recurso. */
public class ResourceStatusService {

    private final ResourceStatusDAO statusDAO = new ResourceStatusDAO();

    public List<ResourceStatus> listarTodos() {
        return statusDAO.buscarTodos();
    }

    public void crear(String nombre, String descripcion, boolean reservable) {
        ValidacionUtil.requerirNoVacio(nombre, "El nombre es obligatorio.");
        try {
            statusDAO.guardar(new ResourceStatus(
                nombre.trim(), vacioANulo(descripcion), reservable));
        } catch (RuntimeException e) {
            throw new BusinessException("No se pudo crear el estado.", e);
        }
    }

    public void actualizar(ResourceStatus estado, String nombre,
                           String descripcion, boolean reservable) {
        ValidacionUtil.requerirNoNulo(estado, "Estado no encontrado.");
        ValidacionUtil.requerirNoVacio(nombre, "El nombre es obligatorio.");

        estado.setNombre(nombre.trim());
        estado.setDescripcion(vacioANulo(descripcion));
        estado.setReservable(reservable);

        try {
            statusDAO.actualizar(estado);
        } catch (RuntimeException e) {
            throw new BusinessException("No se pudo actualizar el estado.", e);
        }
    }

    public void eliminar(Long id) {
        ValidacionUtil.requerirNoNulo(id, "Identificador no válido.");
        try {
            statusDAO.eliminar(id);
        } catch (RuntimeException e) {
            throw new BusinessException("No se puede eliminar: " + e.getMessage(), e);
        }
    }

    private String vacioANulo(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
