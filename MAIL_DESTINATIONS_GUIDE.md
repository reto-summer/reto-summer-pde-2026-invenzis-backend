# Sistema de Destinos de Email - Documentación

## Resumen General

Este módulo permite gestionar dinámicamente los destinos de email (direcciones a las que se envían notificaciones) desde el frontend, sin necesidad de modificar properties o reiniciar la aplicación. Los emails se almacenan en la base de datos en la tabla `destinos_email`.

---

## Estructura de Base de Datos

### Tabla: `destinos_email`

```sql
CREATE TABLE IF NOT EXISTS destinos_email (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    activo BOOLEAN DEFAULT true,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Campos:**
- `id`: Identificador único (autoincremental)
- `email`: Dirección de email (única)
- `activo`: Booleano para habilitar/deshabilitar sin eliminar
- `fecha_creacion`: Registro automático de cuándo se creó
- `fecha_actualizacion`: Se actualiza automáticamente al modificar

---

## Componentes Implementados

### 1. **EmailDestinoModel** (Entidad JPA)
**Archivo:** `src/main/java/com/example/reto_backend_febrero2026/mail/EmailDestinoModel.java`

Entidad que mapea la tabla `destinos_email`. Incluye:
- Anotaciones JPA (`@Entity`, `@Table`)
- Métodos `@PrePersist` y `@PreUpdate` para gestionar automáticamente las fechas
- Constructores y getters/setters

### 2. **MailRepository** (Interfaz Spring Data)
**Archivo:** `src/main/java/com/example/reto_backend_febrero2026/mail/MailRepository.java`

Extiende `JpaRepository<MailDestination, Long>` con métodos personalizados:
- `findByEmail(String email)`: Busca un email específico
- `findAllActiveEmails()`: Lista solo los emails activos como strings
- `findByActivoTrue()`: Retorna todos los destinos activos
- `existsByEmail(String email)`: Verifica existencia de un email

### 3. **MailController** (REST API)
**Archivo:** `src/main/java/com/example/reto_backend_febrero2026/mail/MailController.java`

Endpoint base: `/api/mail-destinations`

#### Endpoints disponibles:

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/` | Obtiene todos los destinos activos |
| GET | `/{id}` | Obtiene un destino específico por ID |
| POST | `/` | Crea un nuevo destino de email |
| PUT | `/{id}` | Actualiza un destino existente |
| DELETE | `/{id}` | Elimina (desactiva) un destino |
| GET | `/active/emails` | Retorna lista de emails activos |

---

## Uso desde el Frontend

### 1. **Crear un destino de email**

```javascript
fetch('http://localhost:8080/api/mail-destinations', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ email: 'usuario@example.com' })
})
.then(res => res.json())
.then(data => console.log(data));
```

**Respuesta exitosa (201):**
```json
{
  "mensaje": "Email registrado exitosamente",
  "id": 1,
  "email": "usuario@example.com",
  "fechaCreacion": "2026-02-23T10:30:00"
}
```

### 2. **Obtener todos los emails activos**

```javascript
fetch('http://localhost:8080/api/mail-destinations')
  .then(res => res.json())
  .then(destinations => console.log(destinations));
```

**Respuesta:**
```json
[
  {
    "id": 1,
    "email": "usuario1@example.com",
    "activo": true,
    "fechaCreacion": "2026-02-23T10:30:00",
    "fechaActualizacion": "2026-02-23T10:30:00"
  },
  {
    "id": 2,
    "email": "usuario2@example.com",
    "activo": true,
    "fechaCreacion": "2026-02-23T11:00:00",
    "fechaActualizacion": "2026-02-23T11:00:00"
  }
]
```

### 3. **Actualizar un email**

```javascript
fetch('http://localhost:8080/api/mail-destinations/1', {
  method: 'PUT',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ email: 'nuevo@example.com', activo: true })
})
.then(res => res.json())
.then(data => console.log(data));
```

### 4. **Eliminar un email (desactivar)**

```javascript
fetch('http://localhost:8080/api/mail-destinations/1', {
  method: 'DELETE'
})
.then(res => res.json())
.then(data => console.log(data));
```

### 5. **Obtener solo emails activos como lista**

```javascript
fetch('http://localhost:8080/api/mail-destinations/active/emails')
  .then(res => res.json())
  .then(emails => console.log(emails)); // ["usuario1@example.com", "usuario2@example.com"]
```

---

## Integración con MailService Existente

El `MailService` actual usa `@Value("${mail.to}")` para leer emails de `application.properties`. Para integrar este nuevo sistema:

**Opción 1: Usar dinámicamente desde el servicio**
```java
@Autowired
private MailRepository mailRepository;

// En lugar de parseEmailList(mailTo), usar:
String[] recipients = mailRepository.findAllActiveEmails().toArray(new String[0]);
```

**Opción 2: Mantener ambos sistemas**
- Configurar property como fallback
- Usar BD cuando esté disponible

---

## Validaciones Implementadas

✅ **Email requerido y no vacío**  
✅ **Formato de email válido** (regex: `^[A-Za-z0-9+_.-]+@(.+)$`)  
✅ **Unicidad de email** (no duplicados)  
✅ **Normalización** (lowercase)  
✅ **Eliminación lógica** (marca como inactivo)  
✅ **Auditoría automática** (fechas de creación/actualización)  

---

## Códigos de Respuesta HTTP

| Código | Situación |
|--------|-----------|
| 200 | Éxito (GET, PUT) |
| 201 | Creado exitosamente (POST) |
| 400 | Solicitud inválida (email mal formateado, vacío) |
| 404 | No encontrado |
| 409 | Conflicto (email duplicado) |
| 500 | Error del servidor |

---

## Flujo Completo desde el Frontend

1. **Usuario carga página** → Llamada GET para cargar emails actuales
2. **Usuario agrega email** → POST con nuevo email
3. **Sistema valida** → Verifica formato, unicidad, registra en BD
4. **Usuario actualiza lista** → GET para refrescar
5. **Sistema envía notificaciones** → MailService lee de BD

---

## Consideraciones Técnicas

- Las fechas se manejan automáticamente con `@PrePersist` y `@PreUpdate`
- Los emails se almacenan en minúsculas para evitar duplicados
- La eliminación es lógica (campo `activo = false`) para mantener auditoría
- El repositorio incluye métodos específicos para obtener solo emails activos
- El controller valida entrada y proporciona mensajes descriptivos
