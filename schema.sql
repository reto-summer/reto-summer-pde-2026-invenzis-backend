CREATE TABLE familias (
    cod         INTEGER      PRIMARY KEY,
    descripcion VARCHAR(255) NOT NULL
);

CREATE TABLE subfamilias (
    fami_cod    INTEGER      NOT NULL,
    cod         INTEGER      NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    PRIMARY KEY (cod),
    FOREIGN KEY (fami_cod) REFERENCES familias(cod)
);

CREATE TABLE licitaciones (
    id_licitacion     SERIAL       PRIMARY KEY,
    titulo            VARCHAR(255) NOT NULL,
    tipo_licitacion   VARCHAR(100) NOT NULL,
    descripcion       TEXT,
    fecha_publicacion DATE        NOT NULL,
    fecha_cierre      DATETIME    NOT NULL,
    link              VARCHAR(500),

    fami_cod          INTEGER,
    subf_cod          INTEGER,

    FOREIGN KEY (fami_cod)
        REFERENCES familias(cod),
    FOREIGN KEY (fami_cod, subf_cod)
        REFERENCES subfamilias(fami_cod, cod)
);
