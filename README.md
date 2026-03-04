# Invenzis Backend

Backend Spring Boot que integra con la plataforma de compras estatales de Uruguay (**ARCE**) para obtener licitaciones vigentes vía RSS, persistirlas en PostgreSQL y notificar a los destinatarios configurados mediante un sistema de notificaciones extensible basado en el **Strategy Pattern** (actualmente soporta email, preparado para nuevos canales).

---

## Stack tecnológico

- **Java 21** + **Spring Boot 3.5.10**
- **PostgreSQL 17** (Supabase en producción, H2 para tests)
- **MapStruct** para mapping Entity-DTO
- **Thymeleaf** para templates de email HTML
- **Spring Retry** con backoff exponencial para resiliencia
- **SpringDoc OpenAPI** (Swagger UI)
- **Google Cloud Run** para deploy

---

## Arquitectura

Arquitectura en capas organizada por dominio:

```
com.example.reto_backend_febrero2026/
├── familia/           # Categorías de productos ARCE
├── subfamilia/        # Subcategorías (clave compuesta)
├── licitacion/        # Licitaciones/tenders
├── email/             # Envío de emails y gestión de destinatarios
├── notificacion/      # Sistema de notificaciones (Strategy Pattern)
│   └── strategy/      # INotificacionStrategy, EmailNotificacionStrategy, Resolver
├── integration/
│   └── servlet/
│       ├── dto/         # Records para parseo XML (RSS y OCDS)
│       └── strategy/    # Strategy pattern para construcción de URLs
├── audit/             # @Auditable annotation + AOP aspect
└── config/            # Async, CORS, Jackson XML, MDC
```

### Flujo principal

```
Scheduler (diario a medianoche UTC-3)
  → ArceClientService fetch RSS de ARCE
  → Parseo XML → List<LicitacionItemRecord>
  → LicitacionService: deduplicacion, limpieza HTML, persistencia
  → EmailService: envio async de licitaciones nuevas
  → AuditAspect: intercepta @Auditable y crea notificacion via Strategy Pattern
  → Marca licitaciones como enviadas
```

### Strategy Pattern — Notificaciones

El sistema de notificaciones usa el patrón Strategy para desacoplar la lógica de cada canal:

```
INotificacionStrategy (interfaz)
├── EmailNotificacionStrategy    → Prefija "[EMAIL]", enriquece detalle con destinatarios activos
└── (extensible a nuevos canales)

NotificacionStrategyResolver     → Auto-descubre strategies via inyección Spring
NotificacionService.create()     → Resuelve strategy → ejecuta send() → persiste en BD
```

**Para agregar un nuevo canal** (ej. WhatsApp, Slack):

1. Crear una implementación de `INotificacionStrategy` como `@Component`
2. Agregar el tipo al enum `NotificacionType`
3. El `NotificacionStrategyResolver` lo detecta automáticamente

---

## Requisitos previos

- **Java 21** (JDK)
- No se necesita Maven instalado (se usa Maven Wrapper)

---

## Configuración

1. Copiar el archivo de ejemplo de variables de entorno:

```bash
cp .env.example .env
```

2. Editar `.env` con tus valores:

```bash
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
POSTGRES_DB=invenzis
POSTGRES_USER=postgres
POSTGRES_PASSWORD=tu_password

SERVER_PORT=8080
CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:3000

MAIL_USERNAME=tu_email@gmail.com
MAIL_PASSWORD=tu_app_password
MAIL_TO=destinatario@ejemplo.com
```

> Para Gmail, necesitas generar una [contraseña de aplicación](https://support.google.com/accounts/answer/185833).

---

## Ejecución

### Desarrollo local

```bash
./mvnw spring-boot:run
```

> Necesitas PostgreSQL accesible (Supabase o local). Configura las variables en `.env`.

### Compilar y empaquetar

```bash
# Compilar (genera mappers de MapStruct)
./mvnw compile

# Ejecutar tests
./mvnw test

# Ejecutar un test específico
./mvnw test -Dtest=LicitacionServiceTest

# Generar JAR sin ejecutar tests
./mvnw package -DskipTests
```

---

## API

**Swagger UI** (OpenAPI 3) — Documentación interactiva:

| Entorno | URL |
|---------|-----|
| Local | `http://localhost:8080/swagger-ui/index.html` |
| QA | `https://qa-reto-summer-pde-2026-invenzis-backend-133459896240.us-east1.run.app/swagger-ui/index.html` |

### Referencia de endpoints

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| **Configuración** | | |
| `GET` | `/config` | Configuración actual (familia, subfamilia) |
| `PUT` | `/config` | Actualizar configuración — *Body:* `{ "familiaCod": 3, "subfamiliaCod": 10 }` |
| **Familias** | | |
| `GET` | `/familias` | Listar categorías ARCE |
| `GET` | `/familia/{cod}` | Familia por código |
| **Subfamilias** | | |
| `GET` | `/subfamilias` | Listar subfamilias |
| `GET` | `/subfamilias/familia/{famiCod}` | Subfamilias por familia |
| `GET` | `/subfamilias/familia/{famiCod}/subfamilia/{cod}` | Subfamilia por clave compuesta |
| **Licitaciones** | | |
| `GET` | `/licitaciones` | Listar — *Params:* `fechaPublicacionDesde/Hasta`, `fechaCierreDesde/Hasta`, `familiaCod`, `subfamiliaCod` |
| `GET` | `/licitaciones/{id}` | Licitación por ID |
| `GET` | `/licitaciones/titulo/{titulo}` | Buscar por título |
| `GET` | `/familias/{familiaCod}/subfamilia/{subfamiliaCod}` | Licitaciones por familia y subfamilia |
| **Email** | | |
| `GET` | `/email` | Destinatarios activos |
| `GET` | `/email/{emailAddress}` | Destinatario por dirección |
| `POST` | `/email` | Crear destinatario — *Body:* `{ "email": "destino@ejemplo.com" }` |
| `DELETE` | `/email/{emailAddress}` | Desactivar destinatario |
| **Notificaciones** | | |
| `GET` | `/notificacion` | Resumen de notificaciones |
| `GET` | `/notificacion/{id}` | Detalle de notificación |
| **Integración ARCE** | | |
| `GET` | `/api/save-rss` | Sincronizar RSS → BD — *Params opc.:* `familyCod`, `subFamilyCod` |
| `GET` | `/api/rss-url` | URL del feed RSS — *Params opc.:* `familyCod`, `subFamilyCod` |

---

## Base de datos

El esquema se gestiona con **JPA/Hibernate** (`spring.jpa.hibernate.ddl-auto=update`):

- **familias** — Categorías de productos ARCE (cod PK)
- **subfamilias** — Subcategorías con clave compuesta (fami_cod + cod)
- **licitacion** — Licitaciones con FK a familia y subfamilia
- **email** — Lista de distribución de destinatarios
- **notificacion** — Registro de notificaciones enviadas por canal
- **config** — Configuración del scheduler (familia, subfamilia)
- **licitacion_email** — Asociación licitación–destinatario para control de envíos

---

## Integración con ARCE

El sistema consume el feed RSS de compras estatales de Uruguay:

```
https://www.comprasestatales.gub.uy/consultas/rss/tipo-pub/VIG/tipo-doc/C/filtro-cat/CAT/familia/{cod}/sub-familia/{cod}
```

### Mecanismo de reintentos

El cliente RSS usa `@Retryable` con backoff exponencial:

- 5 intentos máximos
- Delay inicial: 2 segundos
- Multiplicador: 3x (2s → 6s → 18s → 54s → 162s)

---

## Tests

```bash
# Ejecutar todos los tests
./mvnw test
```

Tests disponibles organizados por dominio:

| Dominio | Tests | Descripción |
|---------|-------|-------------|
| **Familia** | FamiliaControllerTest, FamiliaServiceTest | Consulta de categorías |
| **Subfamilia** | SubfamiliaControllerTest, SubfamiliaServiceTest | Consulta de subcategorías |
| **Licitacion** | LicitacionControllerTest, LicitacionServiceTest | CRUD y búsqueda de licitaciones |
| **Email** | EmailControllerTest, EmailServiceTest | Gestión de destinatarios y envío de emails |
| **Notificacion** | NotificacionControllerTest, NotificacionServiceTest | Servicio y controller de notificaciones |
| **Notificacion Strategy** | EmailNotificacionStrategyTest, NotificacionStrategyResolverTest | Strategy pattern: estrategia email y resolver |
| **Integración** | ArceRssStrategyTest | Estrategia de construcción de URLs RSS |
| **Licitacion-Email** | LicitacionEmailServiceTest | Asociación licitación–email |

Los tests usan **JUnit 5**, **Mockito** y base de datos **H2** en memoria.

---

## Deploy

El proyecto está configurado para deploy en **Google Cloud Run**:

- **GitHub Actions** (`.github/workflows/deploy-dev.yml`): CI/CD en push a rama `dev`
- **Cloud Build** (`cloudbuild.yml`): build y deploy

---

## Documentación

Documentación interactiva disponible en **Swagger UI** (local y QA; ver sección **API**).
