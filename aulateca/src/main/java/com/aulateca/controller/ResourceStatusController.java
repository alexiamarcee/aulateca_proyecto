package com.aulateca.controller;

import com.aulateca.model.ResourceStatus;
import com.aulateca.service.ResourceStatusService;

import java.util.List;
import java.util.Optional;

/** Controlador de estados de recurso. */
public class ResourceStatusController {

    private final ResourceStatusService statusService = new ResourceStatusService();

    public ControllerUtil.Resultado<List<ResourceStatus>> listarEstados() {
        return ControllerUtil.ejecutarConsulta(statusService::listarTodos);
    }

    public Optional<String> crear(String nombre, String descripcion, boolean reservable) {
        return ControllerUtil.ejecutar(() ->
            statusService.crear(nombre, descripcion, reservable));
    }

    public Optional<String> actualizar(ResourceStatus estado, String nombre,
                                       String descripcion, boolean reservable) {
        return ControllerUtil.ejecutar(() ->
            statusService.actualizar(estado, nombre, descripcion, reservable));
    }

    public Optional<String> eliminar(Long id) {
        return ControllerUtil.ejecutar(() -> statusService.eliminar(id));
    }
}
