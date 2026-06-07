package com.aulateca.util;

import com.aulateca.dao.*;
import com.aulateca.model.*;
import com.aulateca.util.PasswordUtil;

import java.time.LocalTime;

/** Carga datos de ejemplo si la BD está vacía. */
public class DataInitializer {

    public static void inicializar() {
        ResourceStatusDAO statusDAO = new ResourceStatusDAO();
        ResourceTypeDAO typeDAO = new ResourceTypeDAO();
        ResourceDAO resourceDAO = new ResourceDAO();
        TimeSlotDAO slotDAO = new TimeSlotDAO();
        UserDAO userDAO = new UserDAO();

        if (userDAO.contar() > 0) return;

        System.out.println("[Aulateca] Inicializando datos de ejemplo...");

        ResourceStatus disponible = new ResourceStatus("Disponible",
            "El recurso está operativo y se puede reservar", true);
        ResourceStatus reservado = new ResourceStatus("Reservado",
            "El recurso tiene reservas activas", true);
        ResourceStatus enUso = new ResourceStatus("En uso",
            "El recurso está siendo utilizado en este momento", true);
        ResourceStatus mantenimiento = new ResourceStatus("Mantenimiento",
            "El recurso está en mantenimiento preventivo o correctivo", false);
        ResourceStatus fueraServicio = new ResourceStatus("Fuera de servicio",
            "El recurso no está operativo", false);

        statusDAO.guardar(disponible);
        statusDAO.guardar(reservado);
        statusDAO.guardar(enUso);
        statusDAO.guardar(mantenimiento);
        statusDAO.guardar(fueraServicio);

        ResourceType tipoAula = new ResourceType("Aula", "Aulas y espacios docentes del centro");
        ResourceType tipoProyector = new ResourceType("Proyector", "Proyectores y equipos de proyección");
        ResourceType tipoLaboratorio = new ResourceType("Laboratorio", "Laboratorios especializados");
        ResourceType tipoCarrito = new ResourceType("Carrito", "Carritos de portátiles y tablets");
        ResourceType tipoSala = new ResourceType("Sala de reuniones", "Salas para reuniones y tutorías");

        typeDAO.guardar(tipoAula);
        typeDAO.guardar(tipoProyector);
        typeDAO.guardar(tipoLaboratorio);
        typeDAO.guardar(tipoCarrito);
        typeDAO.guardar(tipoSala);

        resourceDAO.guardar(new Resource("Aula 1.01", "Planta 1, ala norte, 30 alumnos", tipoAula, disponible, "Planta 1"));
        resourceDAO.guardar(new Resource("Aula 1.02", "Planta 1, ala norte, 30 alumnos", tipoAula, disponible, "Planta 1"));
        resourceDAO.guardar(new Resource("Aula 2.04", "Planta 2, ala sur, 25 alumnos", tipoAula, disponible, "Planta 2"));
        resourceDAO.guardar(new Resource("Aula 3.01", "Planta 3, aula grande, 40 alumnos", tipoAula, disponible, "Planta 3"));
        resourceDAO.guardar(new Resource("Laboratorio de Informática", "20 puestos con PC", tipoLaboratorio, disponible, "Planta 1"));
        resourceDAO.guardar(new Resource("Laboratorio de Ciencias", "Material de experimentos", tipoLaboratorio, disponible, "Planta 2"));
        resourceDAO.guardar(new Resource("Proyector A", "Epson EB-X41, 3600 lúmenes", tipoProyector, disponible, "Conserjería"));
        resourceDAO.guardar(new Resource("Proyector B", "BenQ MX550, portátil", tipoProyector, mantenimiento, "Conserjería"));
        resourceDAO.guardar(new Resource("Carrito Portátiles A", "15 portátiles HP", tipoCarrito, disponible, "Planta 1"));
        resourceDAO.guardar(new Resource("Carrito Portátiles B", "15 portátiles Lenovo", tipoCarrito, disponible, "Planta 2"));
        resourceDAO.guardar(new Resource("Sala de Tutorías", "6 plazas, privada", tipoSala, disponible, "Planta Baja"));

        slotDAO.guardar(new TimeSlot("1ª hora",  LocalTime.of(8,  0), LocalTime.of(9,  0), 1));
        slotDAO.guardar(new TimeSlot("2ª hora",  LocalTime.of(9,  0), LocalTime.of(10, 0), 2));
        slotDAO.guardar(new TimeSlot("3ª hora",  LocalTime.of(10, 0), LocalTime.of(11, 0), 3));
        slotDAO.guardar(new TimeSlot("Recreo",   LocalTime.of(11, 0), LocalTime.of(11,30), 4));
        slotDAO.guardar(new TimeSlot("4ª hora",  LocalTime.of(11,30), LocalTime.of(12,30), 5));
        slotDAO.guardar(new TimeSlot("5ª hora",  LocalTime.of(12,30), LocalTime.of(13,30), 6));
        slotDAO.guardar(new TimeSlot("6ª hora",  LocalTime.of(13,30), LocalTime.of(14,30), 7));
        slotDAO.guardar(new TimeSlot("Tarde 1ª", LocalTime.of(15, 0), LocalTime.of(16, 0), 8));
        slotDAO.guardar(new TimeSlot("Tarde 2ª", LocalTime.of(16, 0), LocalTime.of(17, 0), 9));

        userDAO.guardar(new User("Admin", "Sistema", "admin@aulateca.es",
            PasswordUtil.hash("admin123"), User.Rol.ADMIN));
        userDAO.guardar(new User("María", "García López", "mgarcia@aulateca.es",
            PasswordUtil.hash("prof123"), User.Rol.PROFESOR));
        userDAO.guardar(new User("Carlos", "Martínez Ruiz", "cmartinez@aulateca.es",
            PasswordUtil.hash("prof123"), User.Rol.PROFESOR));
        userDAO.guardar(new User("Ana", "Fernández Pérez", "afernandez@aulateca.es",
            PasswordUtil.hash("alumno123"), User.Rol.ALUMNO));
        userDAO.guardar(new User("Luis", "Sánchez Gómez", "lsanchez@aulateca.es",
            PasswordUtil.hash("alumno123"), User.Rol.ALUMNO));

        System.out.println("[Aulateca] Datos de ejemplo creados correctamente.");
    }
}
