# Invenzis Backend                                                                                            
                                                                                                              
Backend Spring Boot que integra con la plataforma de compras estatales de Uruguay (**ARCE**) para obtener lic 
itaciones vigentes vía RSS, persistirlas en PostgreSQL y notificar por email a los destinatarios configurados 
.                                                                                                             
                                                                                                              
## Stack Tecnologico                                                                                          
                                                                                                              
- **Java 21** + **Spring Boot 3.5.10**                                                                        
- **PostgreSQL 17** (Supabase en produccion, H2 para tests)                                                   
- **MapStruct** para mapping Entity-DTO                                                                       
- **Flyway** para migraciones de base de datos                                                                
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
├── mail/              # Envio de emails y gestion de destinatarios                                           
├── notificacion/      # Log de ejecuciones (auditoria)                                                       
├── integration/                                                                                              
│   └── servlet/                                                                                              
│       ├── controller/  # Endpoints de integracion                                                           
│       ├── service/     # Cliente RSS con reintentos                                                         
│       ├── dto/         # Records para parseo XML (RSS y OCDS)                                               
│       └── strategy/    # Strategy pattern para construccion de URLs                                         
├── audit/             # @Audit annotation + AOP aspect                                                   
└── config/            # Async, CORS, Jackson XML, MDC                                                        
```                                                                                                           
                                                                                                              
### Flujo principal                                                                                           
                                                                                                              
```                                                                                                           
Scheduler (diario a medianoche UTC-3)                                                                         
  → ArceClientService fetch RSS de ARCE                                                                       
  → Parseo XML → List<LicitacionItemRecord>                                                                   
  → LicitacionService: deduplicacion, limpieza HTML, persistencia                                             
  → EmailService: envio async de licitaciones nuevas                                                          
  → Marca licitaciones como enviadas                                                                          
```                                                                                                           
                                                                                                              
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
                                                                                                              
> Para Gmail, necesitas generar una [contrasena de aplicacion](https://support.google.com/accounts/answer/185 
     +833).                                                                                                         
                                                                                                              
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
                                                                                                              
## API Endpoints                                                                                              
                                                                                                              
La documentacion interactiva esta disponible en Swagger UI una vez iniciada la aplicacion:                    
                                                                                                              
- **Swagger UI:** http://localhost:8080/swagger-ui.html                                                       
- **OpenAPI JSON:** http://localhost:8080/reto/v3/api-docs                                                    
                                                                                                              
### Resumen de endpoints                                                                                      
                                                                                                              
| Metodo | Ruta | Descripcion |                                                                               
|--------|------|-------------|                                                                               
| **Familias** | | |                                                                                          
| GET | `/familias` | Listar todas las categorias |                                                           
| GET | `/familia/{cod}` | Obtener categoria por codigo |                                                     
| **Subfamilias** | | |                                                                                       
| GET | `/subfamilias` | Listar todas las subcategorias |                                                     
| GET | `/subfamilias/familia/{famiCod}` | Filtrar por categoria padre |                                      
| **Licitaciones** | | |                                                                                      
| GET | `/licitaciones/{id}` | Obtener licitacion por ID |                                                    
| GET | `/licitacion/titulo/{titulo}` | Buscar por titulo |                                                   
| GET | `/familias/{familiaCod}/subfamilia/{subfamiliaCod}` | Filtrar por categoria/subcategoria |            
| **Email** | | |                                                                                             
| GET | `/email` | Listar destinatarios activos |                                                             
| POST | `/email` | Agregar destinatario |                                                                    
| PUT | `/email/{address}` | Activar/desactivar destinatario |                                                
| DELETE | `/email/{address}` | Eliminar destinatario |                                                       
| **Notificaciones** | | |                                                                                    
| GET | `/notificaciones` | Listar logs de ejecucion |                                                        
| GET | `/notificaciones/{id}` | Detalle de una ejecucion |                                                   
                                                                                                              
## Base de datos                                                                                              
El esquema se gestiona con Flyway (migraciones en `src/main/resources/db/migration/`):                        
                                                                                                              
- **familias** - Categorias de productos ARCE (cod PK)                                                        
- **subfamilias** - Subcategorias con clave compuesta (fami_cod + cod)                                        
- **licitacion** - Licitaciones con FK a familia y subfamilia                                                 
- **destinos_email** - Lista de distribucion de emails                                                        
- **notificacion** - Log de ejecuciones del scheduler                                                         
                                                                                                              
Los datos semilla (10 familias, 37 subfamilias) se cargan automaticamente con `V2_insert_data.sql`.           
                                                                                                              
## Integracion con ARCE                                                                                       
                                                                                                              
El sistema consume el feed RSS de compras estatales de Uruguay:                                               
                                                                                                              
```                                                                                                           
https://www.comprasestatales.gub.uy/consultas/rss/tipo-pub/VIG/tipo-doc/C/filtro-cat/CAT/familia/{cod}/sub-fa 
milia/{cod}                                                                                                   
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
                                                                                                              
# Tests disponibles:                                                                                          
- FamiliaControllerTest, FamiliaServiceTest                                                                 
- SubfamiliaControllerTest, SubfamiliaServiceTest                                                           
- LicitacionControllerTest, LicitacionServiceTest                                                           
- ArceRssStrategyTest                                                                                       
```                                                                                                           
                                                                                                              
Los tests usan **JUnit 5** con **Mockito** y base de datos **H2** en memoria.                                 
                                                                                                              
## Deploy                                                                                                     
                                                                                                              
El proyecto esta configurado para deploy en **Google Cloud Run**:                                             
                                                                                                              
- **GitHub Actions** (`.github/workflows/deploy-dev.yml`): CI/CD en push a rama `dev`                         
- **Cloud Build** (`cloudbuild.yml`): Build y deploy del contenedor Docker                                    
- **Dockerfile**: Build multi-stage (Maven build + JRE runtime Alpine)                                        
                                                                                                              
## Documentacion                                                                                               
                                                                                                              
La documentacion se genera con **Swagger** y se puede acceder a ella en:                                        
                                                                                                              
```                                                                                                            
http://localhost:8080/swagger-ui/index.html                                                                    
```                                                                                                            