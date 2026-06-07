package com.aulateca.util;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/** Catálogo de recursos predefinidos por tipo (nombre y ubicación implícita). */
public final class ResourceCatalog {

    public record Opcion(String nombre, String ubicacion) {}

    private static final Map<String, List<Opcion>> POR_TIPO = Map.of(
        "Aula", List.of(
            new Opcion("Aula 200", "Planta 2"),
            new Opcion("Aula 201", "Planta 2"),
            new Opcion("Aula 202", "Planta 2")
        ),
        "Carrito", List.of(
            new Opcion("Carrito A", "Planta 1"),
            new Opcion("Carrito B", "Planta 2"),
            new Opcion("Carrito C", "Planta 3")
        ),
        "Proyector", List.of(
            new Opcion("Proyector 1", "Conserjería"),
            new Opcion("Proyector 2", "Conserjería"),
            new Opcion("Proyector 3", "Conserjería")
        ),
        "Laboratorio", List.of(
            new Opcion("Laboratorio de informática", "Planta 1"),
            new Opcion("Laboratorio de ciencias", "Planta 2"),
            new Opcion("Laboratorio de biología", "Planta 3")
        ),
        "Sala de reuniones", List.of(
            new Opcion("Sala de reuniones del ala sur", "Ala sur"),
            new Opcion("Sala de reuniones del ala este", "Ala este")
        )
    );

    private ResourceCatalog() {}

    public static List<Opcion> opcionesPorTipo(String nombreTipo) {
        return POR_TIPO.getOrDefault(nombreTipo, List.of());
    }

    public static boolean tieneCatalogo(String nombreTipo) {
        return POR_TIPO.containsKey(nombreTipo);
    }

    public static Optional<String> buscarUbicacion(String nombreTipo, String nombreRecurso) {
        return opcionesPorTipo(nombreTipo).stream()
            .filter(o -> o.nombre().equals(nombreRecurso))
            .map(Opcion::ubicacion)
            .findFirst();
    }

    public static boolean quedanOpcionesDisponibles(Set<String> nombresUsados) {
        return POR_TIPO.values().stream()
            .flatMap(List::stream)
            .anyMatch(o -> !nombresUsados.contains(o.nombre()));
    }
}
