package com.aulateca.controller;

import com.aulateca.model.ResourceType;
import com.aulateca.service.ResourceTypeService;

import java.util.List;
import java.util.Optional;

/** Controlador de tipos de recurso. */
public class ResourceTypeController {

    private final ResourceTypeService typeService = new ResourceTypeService();

    public ControllerUtil.Resultado<List<ResourceType>> listarTipos() {
        return ControllerUtil.ejecutarConsulta(typeService::listarTodos);
    }

    public Optional<String> crear(String nombre, String descripcion) {
        return ControllerUtil.ejecutar(() -> typeService.crear(nombre, descripcion));
    }

    public Optional<String> actualizar(ResourceType tipo, String nombre, String descripcion) {
        return ControllerUtil.ejecutar(() -> typeService.actualizar(tipo, nombre, descripcion));
    }

    public Optional<String> eliminar(Long id) {
        return ControllerUtil.ejecutar(() -> typeService.eliminar(id));
    }
}
