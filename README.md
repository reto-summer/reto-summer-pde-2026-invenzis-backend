# Invenzis Backend

Backend Spring Boot que integra con la plataforma de compras estatales de Uruguay (**ARCE**) para obtener licitaciones vigentes vía RSS, persistirlas en PostgreSQL y notificar a los destinatarios configurados mediante un sistema de notificaciones extensible basado en el **Strategy Pattern** (actualmente soporta email, preparado para nuevos canales).

## Stack Tecnologico

- **Java 21** + **Spring Boot 3.5.10**
- **PostgreSQL 17** (Supabase en produccion, H2 para tests)
- **MapStruct** para mapping Entity-DTO
- **Thymeleaf** para templates de email HTML
- **Spring Retry** con backoff exponencial para resiliencia
- **SpringDoc OpenAPI** (Swagger UI)
- **Docker** + **Google Cloud Run** para deploy

## Arquitectura

Arquitectura en capas organizada por dominio:

```
com.example.reto_backend_febrero2026/
├── familia/           # Categorias de productos ARCE
├── subfamilia/        # Subcategorias (clave compuesta)
├── licitacion/        # Licitaciones/tenders
├── email/             # Envio de emails y gestion de destinatarios
├── notificacion/      # Sistema de notificaciones (Strategy Pattern)
│   └── strategy/      # INotificacionStrategy, EmailNotificacionStrategy, Resolver
├── integration/
│   └── servlet/
│       ├── controller/  # Endpoints de integracion
│       ├── service/     # Cliente RSS con reintentos
│       ├── dto/         # Records para parseo XML (RSS y OCDS)
│       └── strategy/    # Strategy pattern para construccion de URLs
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

## Requisitos previos

- **Java 21** (JDK)
- **Docker** y **Docker Compose** (opcional, para base de datos local)
- No se necesita Maven instalado (se usa Maven Wrapper)

## Configuracion

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

> Para Gmail, necesitas generar una [contrasena de aplicacion](https://support.google.com/accounts/answer/185833).

## Ejecucion

### Desarrollo local

```bash
# Levantar solo PostgreSQL con Docker
docker compose up -d postgres

# Iniciar la aplicacion
./mvnw spring-boot:run
```

### Con Docker (aplicacion completa)

```bash
docker compose up -d
```

### Compilar y empaquetar

```bash
# Compilar (genera mappers de MapStruct)
./mvnw compile

# Ejecutar tests
./mvnw test

# Ejecutar un test especifico
./mvnw test -Dtest=LicitacionServiceTest

# Generar JAR sin ejecutar tests
./mvnw package -DskipTests
```

## API

**Swagger UI** (OpenAPI 3) — Documentación interactiva:

| Entorno | URL |
|---------|-----|
| Local | `http://localhost:8080/swagger-ui/index.html` |
| QA    | `https://qa-reto-summer-pde-2026-invenzis-backend-133459896240.us-east1.run.app/swagger-ui/index.html` |

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

## Base de datos

El esquema se gestiona con **JPA/Hibernate** (`spring.jpa.hibernate.ddl-auto=update`):

- **familias** — Categorias de productos ARCE (cod PK)
- **subfamilias** — Subcategorias con clave compuesta (fami_cod + cod)
- **licitacion** — Licitaciones con FK a familia y subfamilia
- **email** — Lista de distribucion de destinatarios
- **notificacion** — Registro de notificaciones enviadas por canal
- **config** — Configuracion del scheduler (familia, subfamilia)
- **licitacion_email** — Asociacion licitacion-destinatario para control de envios

## Integracion con ARCE

El sistema consume el feed RSS de compras estatales de Uruguay:

```
https://www.comprasestatales.gub.uy/consultas/rss/tipo-pub/VIG/tipo-doc/C/filtro-cat/CAT/familia/{cod}/sub-familia/{cod}
```

### Mecanismo de reintentos

El cliente RSS usa `@Retryable` con backoff exponencial:
- 5 intentos maximos
- Delay inicial: 2 segundos
- Multiplicador: 3x (2s → 6s → 18s → 54s → 162s)

## Tests

```bash
# Ejecutar todos los tests
./mvnw test
```

Tests disponibles organizados por dominio:

| Dominio | Tests | Descripcion |
|---------|-------|-------------|
| **Familia** | FamiliaControllerTest, FamiliaServiceTest | Consulta de categorias |
| **Subfamilia** | SubfamiliaControllerTest, SubfamiliaServiceTest | Consulta de subcategorias |
| **Licitacion** | LicitacionControllerTest, LicitacionServiceTest | CRUD y busqueda de licitaciones |
| **Email** | EmailControllerTest, EmailServiceTest | Gestion de destinatarios y envio de emails |
| **Notificacion** | NotificacionControllerTest, NotificacionServiceTest | Servicio y controller de notificaciones |
| **Notificacion Strategy** | EmailNotificacionStrategyTest, NotificacionStrategyResolverTest | Strategy pattern: estrategia email y resolver |
| **Integracion** | ArceRssStrategyTest | Estrategia de construccion de URLs RSS |
| **Licitacion-Email** | LicitacionEmailServiceTest | Asociacion licitacion-email |

Los tests usan **JUnit 5** con **Mockito** y base de datos **H2** en memoria.

## Deploy

El proyecto esta configurado para deploy en **Google Cloud Run**:

- **GitHub Actions** (`.github/workflows/deploy-dev.yml`): CI/CD en push a rama `dev`
- **Cloud Build** (`cloudbuild.yml`): Build y deploy del contenedor Docker
- **Dockerfile**: Build multi-stage (Maven build + JRE runtime Alpine)

## Documentación

Documentación interactiva disponible en **Swagger UI** (local y QA, ver sección API).
