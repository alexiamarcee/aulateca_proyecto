# Aulateca – Gestor de Reservas de Espacios y Recursos del Centro

> Proyecto final UT8 · 1.º DAW · Java · Swing · Hibernate · MySQL/MariaDB · Maven · MVC

---

## Descripción

**Aulateca** es una aplicación de escritorio desarrollada en Java que permite gestionar las reservas de aulas, laboratorios, proyectores y otros recursos de un centro educativo. Ofrece una interfaz gráfica completa con Swing, persistencia mediante Hibernate y una arquitectura organizada en capas (MVC + DAO + Service), con jerarquía de excepciones propia, hashing seguro de contraseñas con BCrypt y un selector visual de fechas con JCalendar.

---

## Tecnologías

| Tecnología | Versión | Uso |
|---|---|---|
| Java | 17 | Lenguaje principal |
| Maven | 3.x | Gestión del proyecto |
| Hibernate ORM | 6.4.4.Final | Persistencia (JPA) |
| Jakarta Persistence API | 3.1.0 | Especificación JPA |
| MySQL / MariaDB | 8+ / 10.6+ | Base de datos |
| MySQL Connector/J | 8.3.0 | Driver JDBC para MySQL |
| MariaDB Java Client | 3.3.3 | Driver JDBC alternativo para MariaDB |
| Java Swing | JDK | Interfaz gráfica |
| JCalendar | 1.4 | Selector visual de fechas en Swing |
| jBCrypt | 0.4 | Hash seguro de contraseñas |
| Git / GitHub | — | Control de versiones |

---

## Estructura del proyecto

```
aulateca/
├── src/main/java/com/aulateca/
│   ├── Main.java                            ← Punto de entrada
│   ├── model/                               ← Entidades JPA
│   │   ├── User.java
│   │   ├── ResourceType.java
│   │   ├── ResourceStatus.java
│   │   ├── Resource.java
│   │   ├── TimeSlot.java
│   │   └── Reservation.java
│   ├── dao/                                 ← Capa de persistencia (DAO)
│   │   ├── GenericDAO.java
│   │   ├── UserDAO.java
│   │   ├── ResourceDAO.java
│   │   ├── ResourceTypeDAO.java
│   │   ├── ResourceStatusDAO.java
│   │   ├── TimeSlotDAO.java
│   │   └── ReservationDAO.java
│   ├── service/                             ← Lógica de negocio
│   │   ├── ReservationService.java
│   │   ├── AvailabilityService.java
│   │   ├── DashboardService.java
│   │   ├── ResourceService.java
│   │   ├── ResourceStatusService.java
│   │   ├── ResourceTypeService.java
│   │   ├── TimeSlotService.java
│   │   ├── UserService.java
│   │   ├── ValidacionUtil.java
│   │   └── dto/                             ← Objetos de transferencia de datos
│   │       ├── DashboardStats.java
│   │       ├── DisponibilidadCheckResult.java
│   │       └── DisponibilidadResultado.java
│   ├── controller/                          ← Controladores MVC
│   │   ├── AvailabilityController.java
│   │   ├── ControllerUtil.java
│   │   ├── DashboardController.java
│   │   ├── LoginController.java
│   │   ├── ReservationController.java
│   │   ├── ResourceController.java
│   │   ├── ResourceStatusController.java
│   │   ├── ResourceTypeController.java
│   │   ├── TimeSlotController.java
│   │   └── UserController.java
│   ├── exception/                           ← Jerarquía de excepciones propia
│   │   ├── AulatecaException.java
│   │   ├── BusinessException.java
│   │   └── ValidationException.java
│   ├── util/                                ← Utilidades
│   │   ├── HibernateUtil.java
│   │   ├── DataInitializer.java
│   │   ├── PasswordUtil.java
│   │   └── ResourceCatalog.java
│   └── view/                                ← Interfaz gráfica Swing
│       ├── AppColors.java
│       ├── UIFactory.java
│       ├── LoginDialog.java
│       ├── MainFrame.java
│       └── panels/
│           ├── DashboardPanel.java
│           ├── ReservationsPanel.java
│           ├── ReservationFormDialog.java
│           ├── AvailabilityPanel.java
│           ├── ResourcesPanel.java
│           ├── ResourceTypesPanel.java
│           ├── ResourceStatusPanel.java
│           ├── TimeSlotsPanel.java
│           └── UsersPanel.java
├── src/main/resources/
│   └── META-INF/persistence.xml            ← Configuración Hibernate
├── aulateca_schema.sql                      ← Script SQL opcional
└── pom.xml                                  ← Dependencias Maven
```

---

## Requisitos previos

- **JDK 17** o superior
- **Maven 3.6+**
- **MySQL 8+** o **MariaDB 10.6+** en ejecución

---

## Configuración de la base de datos

### Opción A – Dejar que Hibernate la cree automáticamente (recomendado)

Hibernate tiene configurado `hbm2ddl.auto = update`, por lo que creará las tablas automáticamente al arrancar si no existen.

Solo necesitas crear la base de datos vacía:

```sql
CREATE DATABASE aulateca CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### Opción B – Script SQL manual

```bash
mysql -u root -p < aulateca_schema.sql
```

---

## Configurar la conexión

Edita el archivo `src/main/resources/META-INF/persistence.xml` y ajusta:

```xml
<property name="jakarta.persistence.jdbc.url"
          value="jdbc:mysql://localhost:3306/aulateca?..."/>
<property name="jakarta.persistence.jdbc.user"     value="root"/>
<property name="jakarta.persistence.jdbc.password" value="TU_CONTRASEÑA"/>
```

Para **MariaDB**, cambia también el driver y el dialecto:

```xml
<property name="jakarta.persistence.jdbc.driver"  value="org.mariadb.jdbc.Driver"/>
<property name="jakarta.persistence.jdbc.url"
          value="jdbc:mariadb://localhost:3306/aulateca"/>
<property name="hibernate.dialect"  value="org.hibernate.dialect.MariaDBDialect"/>
```

---

## Compilar y ejecutar

```bash
# Compilar y generar el JAR con todas las dependencias
mvn clean package -q

# Ejecutar
java -jar target/aulateca-1.0-SNAPSHOT-jar-with-dependencies.jar
```

O directamente con Maven:

```bash
mvn exec:java -Dexec.mainClass="com.aulateca.Main"
```

---

## Credenciales de acceso por defecto

| Rol | Email | Contraseña |
|---|---|---|
| Administrador | `admin@aulateca.es` | `admin123` |
| Profesora | `mgarcia@aulateca.es` | `prof123` |
| Alumno | `afernandez@aulateca.es` | `alumno123` |

> Las contraseñas se almacenan hasheadas con **BCrypt** (12 rondas). Los datos de ejemplo se insertan automáticamente la primera vez que arranca la aplicación si la base de datos está vacía.

---

## Funcionalidades

### Módulo de Reservas
- Crear, modificar y cancelar reservas
- Validación de conflictos (no permite reservas duplicadas)
- Comprobación previa de disponibilidad con botón dedicado
- Filtros: todas, próximas, hoy, este mes
- Vista de detalle con doble clic

### Panel de Disponibilidad
- Cuadrícula visual recursos × franjas horarias
- Filtro por tipo de recurso y fecha
- Código de colores: verde (libre), rojo (ocupado), gris (no reservable)

### Gestión de Recursos
- CRUD completo de recursos, tipos, estados y franjas horarias
- Estados con indicador de "reservable" para bloquear reservas

### Gestión de Usuarios (solo ADMIN)
- CRUD completo de usuarios
- Roles: ADMIN, PROFESOR, ALUMNO
- Activar / desactivar usuarios

### Dashboard
- Tarjetas con estadísticas en tiempo real
- Tabla de reservas del día actual

---

## Arquitectura y patrones

```
Vista (Swing panels)
    ↓ eventos de usuario
Controlador (controller/)
    ↓ llama a
Servicio (service/) ← reglas de negocio + DTOs
    ↓ llama a
DAO (dao/GenericDAO + específicos) ← acceso a datos
    ↓ usa
Hibernate / JPA ← ORM
    ↓
MySQL / MariaDB
```

### Patrones aplicados

- **MVC**: capa `controller/` separada de la vista (`view/`) y la lógica (`service/`)
- **DAO** (Data Access Object): cada entidad tiene su propio DAO que extiende `GenericDAO<T, ID>`
- **DTO** (Data Transfer Object): objetos en `service/dto/` para comunicar datos entre capas sin exponer entidades JPA
- **Singleton**: `HibernateUtil` gestiona el `EntityManagerFactory`
- **Factory**: `UIFactory` centraliza la creación de componentes Swing
- **Jerarquía de excepciones propia**: `AulatecaException` → `BusinessException` / `ValidationException`

---

## Reglas de negocio principales

1. No se puede reservar un recurso cuyo estado tenga `reservable = false`
2. No se pueden crear reservas para fechas pasadas
3. No pueden existir dos reservas **confirmadas** para el mismo recurso, fecha y franja (restricción a nivel de BD y de servicio)
4. No se puede modificar una reserva cancelada
5. Los usuarios inactivos no pueden autenticarse
6. Al cancelar una reserva, no se elimina físicamente: cambia su estado a `CANCELADA`
7. Solo el ADMIN puede gestionar reservas de otros usuarios; PROFESOR y ALUMNO solo pueden gestionar las propias

