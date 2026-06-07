package com.aulateca.service;

import com.aulateca.dao.ResourceTypeDAO;
import com.aulateca.exception.BusinessException;
import com.aulateca.model.ResourceType;

import java.util.List;

/** Lógica de negocio de tipos de recurso. */
public class ResourceTypeService {

    private final ResourceTypeDAO typeDAO = new ResourceTypeDAO();

    public List<ResourceType> listarTodos() {
        return typeDAO.buscarTodos();
    }

    public void crear(String nombre, String descripcion) {
        ValidacionUtil.requerirNoVacio(nombre, "El nombre es obligatorio.");
        try {
            typeDAO.guardar(new ResourceType(nombre.trim(), vacioANulo(descripcion)));
        } catch (RuntimeException e) {
            throw new BusinessException("No se pudo crear el tipo.", e);
        }
    }

    public void actualizar(ResourceType tipo, String nombre, String descripcion) {
        ValidacionUtil.requerirNoNulo(tipo, "Tipo no encontrado.");
        ValidacionUtil.requerirNoVacio(nombre, "El nombre es obligatorio.");

        tipo.setNombre(nombre.trim());
        tipo.setDescripcion(vacioANulo(descripcion));

        try {
            typeDAO.actualizar(tipo);
        } catch (RuntimeException e) {
            throw new BusinessException("No se pudo actualizar el tipo.", e);
        }
    }

    public void eliminar(Long id) {
        ValidacionUtil.requerirNoNulo(id, "Identificador no válido.");
        try {
            typeDAO.eliminar(id);
        } catch (RuntimeException e) {
            throw new BusinessException("No se puede eliminar: " + e.getMessage(), e);
        }
    }

    private String vacioANulo(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
