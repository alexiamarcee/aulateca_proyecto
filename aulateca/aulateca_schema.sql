-- ============================================================
--  AULATECA – Script SQL de creación de base de datos
--  Compatible con MySQL 8+ y MariaDB 10.6+
--  Hibernate gestiona las tablas automáticamente (hbm2ddl=update)
--  Este script es una referencia manual o para entornos sin DDL auto
-- ============================================================

CREATE DATABASE IF NOT EXISTS aulateca
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE aulateca;

-- ── Tabla: resource_status ──────────────────────────────────
CREATE TABLE IF NOT EXISTS resource_status (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(80)  NOT NULL UNIQUE,
    descripcion VARCHAR(200),
    reservable  TINYINT(1)   NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── Tabla: resource_types ───────────────────────────────────
CREATE TABLE IF NOT EXISTS resource_types (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── Tabla: resources ────────────────────────────────────────
CREATE TABLE IF NOT EXISTS resources (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    ubicacion   VARCHAR(100),
    tipo_id     BIGINT NOT NULL,
    estado_id   BIGINT NOT NULL,
    CONSTRAINT fk_resource_tipo   FOREIGN KEY (tipo_id)   REFERENCES resource_types(id),
    CONSTRAINT fk_resource_estado FOREIGN KEY (estado_id) REFERENCES resource_status(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── Tabla: time_slots ───────────────────────────────────────
CREATE TABLE IF NOT EXISTS time_slots (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(80)  NOT NULL UNIQUE,
    hora_inicio  TIME NOT NULL,
    hora_fin     TIME NOT NULL,
    orden        INT  NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── Tabla: users ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre    VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    email     VARCHAR(150) NOT NULL UNIQUE,
    password  VARCHAR(60)  NOT NULL,
    rol       ENUM('ADMIN','PROFESOR','ALUMNO') NOT NULL,
    activo    TINYINT(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── Tabla: reservations ──────────────────────────────────────
CREATE TABLE IF NOT EXISTS reservations (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id         BIGINT   NOT NULL,
    recurso_id         BIGINT   NOT NULL,
    fecha              DATE     NOT NULL,
    franja_horaria_id  BIGINT   NOT NULL,
    motivo             VARCHAR(255),
    estado             ENUM('CONFIRMADA','CANCELADA','PENDIENTE') NOT NULL DEFAULT 'CONFIRMADA',
    fecha_creacion     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion DATETIME,

    -- Restricción clave: evita reservas duplicadas
    CONSTRAINT uk_recurso_fecha_franja UNIQUE (recurso_id, fecha, franja_horaria_id),

    CONSTRAINT fk_res_usuario   FOREIGN KEY (usuario_id)        REFERENCES users(id),
    CONSTRAINT fk_res_recurso   FOREIGN KEY (recurso_id)         REFERENCES resources(id),
    CONSTRAINT fk_res_franja    FOREIGN KEY (franja_horaria_id)  REFERENCES time_slots(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
--  Datos de ejemplo (los mismos que inserta DataInitializer.java)
-- ============================================================

INSERT IGNORE INTO resource_status (nombre, descripcion, reservable) VALUES
('Disponible',      'Operativo y reservable',                        1),
('Reservado',       'Tiene reservas activas',                        1),
('En uso',          'Siendo utilizado en este momento',              1),
('Mantenimiento',   'En mantenimiento preventivo o correctivo',      0),
('Fuera de servicio','No operativo',                                  0);

INSERT IGNORE INTO resource_types (nombre, descripcion) VALUES
('Aula',            'Aulas y espacios docentes'),
('Proyector',       'Proyectores y equipos de proyección'),
('Laboratorio',     'Laboratorios especializados'),
('Carrito',         'Carritos de portátiles y tablets'),
('Sala de reuniones','Salas para reuniones y tutorías');

INSERT IGNORE INTO time_slots (nombre, hora_inicio, hora_fin, orden) VALUES
('1ª hora',  '08:00', '09:00', 1),
('2ª hora',  '09:00', '10:00', 2),
('3ª hora',  '10:00', '11:00', 3),
('Recreo',   '11:00', '11:30', 4),
('4ª hora',  '11:30', '12:30', 5),
('5ª hora',  '12:30', '13:30', 6),
('6ª hora',  '13:30', '14:30', 7),
('Tarde 1ª', '15:00', '16:00', 8),
('Tarde 2ª', '16:00', '17:00', 9);

INSERT IGNORE INTO users (nombre, apellidos, email, password, rol, activo) VALUES
('Admin',  'Sistema',         'admin@aulateca.es',    'admin123',   'ADMIN',   1),
('María',  'García López',    'mgarcia@aulateca.es',  'prof123',    'PROFESOR',1),
('Carlos', 'Martínez Ruiz',   'cmartinez@aulateca.es','prof123',    'PROFESOR',1),
('Ana',    'Fernández Pérez', 'afernandez@aulateca.es','alumno123', 'ALUMNO',  1),
('Luis',   'Sánchez Gómez',   'lsanchez@aulateca.es', 'alumno123', 'ALUMNO',  1);
