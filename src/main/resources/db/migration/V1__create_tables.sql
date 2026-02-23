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
)
