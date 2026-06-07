package com.aulateca.service;

import com.aulateca.dao.ResourceDAO;
import com.aulateca.dao.ResourceStatusDAO;
import com.aulateca.dao.ResourceTypeDAO;
import com.aulateca.exception.BusinessException;
import com.aulateca.exception.ValidationException;
import com.aulateca.model.Resource;
import com.aulateca.model.ResourceStatus;
import com.aulateca.model.ResourceType;

import java.util.List;

/** Lógica de negocio de recursos. */
public class ResourceService {

    private final ResourceDAO resourceDAO = new ResourceDAO();
    private final ResourceTypeDAO typeDAO = new ResourceTypeDAO();
    private final ResourceStatusDAO statusDAO = new ResourceStatusDAO();

    public List<Resource> listarTodos() {
        return resourceDAO.buscarTodos();
    }

    public List<ResourceType> listarTipos() {
        return typeDAO.buscarTodos();
    }

    public List<ResourceStatus> listarEstados() {
        return statusDAO.buscarTodos();
    }

    /** Crea un recurso validando los datos obligatorios. */
    public void crear(String nombre, String descripcion, ResourceType tipo,
                      ResourceStatus estado, String ubicacion) {
        validarDatos(nombre, tipo, estado);
        Resource recurso = new Resource(
            nombre.trim(),
            vacioANulo(descripcion),
            tipo,
            estado,
            vacioANulo(ubicacion));
        try {
            resourceDAO.guardar(recurso);
        } catch (RuntimeException e) {
            throw new BusinessException("No se pudo crear el recurso.", e);
        }
    }

    /** Actualiza un recurso existente. */
    public void actualizar(Resource recurso, String nombre, String descripcion,
                           ResourceType tipo, ResourceStatus estado, String ubicacion) {
        ValidacionUtil.requerirNoNulo(recurso, "Recurso no encontrado.");
        validarDatos(nombre, tipo, estado);

        recurso.setNombre(nombre.trim());
        recurso.setDescripcion(vacioANulo(descripcion));
        recurso.setTipo(tipo);
        recurso.setEstado(estado);
        recurso.setUbicacion(vacioANulo(ubicacion));

        try {
            resourceDAO.actualizar(recurso);
        } catch (RuntimeException e) {
            throw new BusinessException("No se pudo actualizar el recurso.", e);
        }
    }

    /** Elimina un recurso por su identificador. */
    public void eliminar(Long id) {
        ValidacionUtil.requerirNoNulo(id, "Identificador de recurso no válido.");
        try {
            resourceDAO.eliminar(id);
        } catch (RuntimeException e) {
            throw new BusinessException("No se puede eliminar el recurso: " + e.getMessage(), e);
        }
    }

    private void validarDatos(String nombre, ResourceType tipo, ResourceStatus estado) {
        ValidacionUtil.requerirNoVacio(nombre, "El nombre es obligatorio.");
        ValidacionUtil.requerirNoNulo(tipo, "Debe seleccionar un tipo de recurso.");
        ValidacionUtil.requerirNoNulo(estado, "Debe seleccionar un estado.");
    }

    private String vacioANulo(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
