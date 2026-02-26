# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .

# Descarga de dependencias (cacheado)
RUN mvn dependency:go-offline -B
COPY src ./src

# Se agregan flags para asegurar que el build no intente conectar a bases de datos o correr tests
RUN mvn package -DskipTests -Dmaven.test.skip=true -B

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Usamos un wildcard (*) para evitar errores si el nombre del .jar cambia ligeramente
COPY --from=build /app/target/*.jar app.jar

# Cloud Run inyecta la variable $PORT. 
# Este comando obliga a Spring Boot a usar ese puerto específico.
ENTRYPOINT ["java", "-Dserver.port=8000", "-jar", "app.jar"]
