CREATE TABLE IF NOT EXISTS familias (
    cod INTEGER PRIMARY KEY,
    descripcion VARCHAR(255),
    comprable CHAR(1)
);

CREATE TABLE IF NOT EXISTS subfamilias(
    cod INTEGER PRIMARY KEY,
    fami_cod INTEGER,
    descripcion VARCHAR (255)

    CONSTRAINT fk_familia
        FOREIGN KEY (fami_cod)
        REFERENCES familias(cod)
);

CREATE TABLE IF NOT EXISTS destinos_email (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    activo BOOLEAN DEFAULT true,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS notificacion (
    id_notificaciones INTEGER PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    exito BOOLEAN NOT NULL,
    detalle TEXT NULL,
    contenido TEXT NULL,
    fecha_ejecucion TIMESTAMP NOT NULL
);