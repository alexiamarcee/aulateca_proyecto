package com.aulateca.controller;

import com.aulateca.model.Resource;
import com.aulateca.model.ResourceStatus;
import com.aulateca.model.ResourceType;
import com.aulateca.service.ResourceService;

import java.util.List;
import java.util.Optional;

/** Controlador de gestión de recursos. */
public class ResourceController {

    private final ResourceService resourceService = new ResourceService();

    public ControllerUtil.Resultado<List<Resource>> listarRecursos() {
        return ControllerUtil.ejecutarConsulta(resourceService::listarTodos);
    }

    public ControllerUtil.Resultado<List<ResourceType>> listarTipos() {
        return ControllerUtil.ejecutarConsulta(resourceService::listarTipos);
    }

    public ControllerUtil.Resultado<List<ResourceStatus>> listarEstados() {
        return ControllerUtil.ejecutarConsulta(resourceService::listarEstados);
    }

    public Optional<String> crear(String nombre, String descripcion,
                                  ResourceType tipo, ResourceStatus estado, String ubicacion) {
        return ControllerUtil.ejecutar(() ->
            resourceService.crear(nombre, descripcion, tipo, estado, ubicacion));
    }

    public Optional<String> actualizar(Resource recurso, String nombre, String descripcion,
                                       ResourceType tipo, ResourceStatus estado, String ubicacion) {
        return ControllerUtil.ejecutar(() ->
            resourceService.actualizar(recurso, nombre, descripcion, tipo, estado, ubicacion));
    }

    public Optional<String> eliminar(Long id) {
        return ControllerUtil.ejecutar(() -> resourceService.eliminar(id));
    }
}
