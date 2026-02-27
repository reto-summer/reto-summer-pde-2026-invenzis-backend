 +# Invenzis Backend                                                                                            
    2 +                                                                                                              
    3 +Backend Spring Boot que integra con la plataforma de compras estatales de Uruguay (**ARCE**) para obtener lic 
      +itaciones vigentes vía RSS, persistirlas en PostgreSQL y notificar por email a los destinatarios configurados 
      +.                                                                                                             
    4 +                                                                                                              
    5 +## Stack Tecnologico                                                                                          
    6 +                                                                                                              
    7 +- **Java 21** + **Spring Boot 3.5.10**                                                                        
    8 +- **PostgreSQL 17** (Supabase en produccion, H2 para tests)                                                   
    9 +- **MapStruct** para mapping Entity-DTO                                                                       
   10 +- **Flyway** para migraciones de base de datos                                                                
   11 +- **Thymeleaf** para templates de email HTML                                                                  
   12 +- **Spring Retry** con backoff exponencial para resiliencia                                                   
   13 +- **SpringDoc OpenAPI** (Swagger UI)                                                                          
   14 +- **Docker** + **Google Cloud Run** para deploy                                                               
   15 +                                                                                                              
   16 +## Arquitectura                                                                                               
   17 +                                                                                                              
   18 +Arquitectura en capas organizada por dominio:                                                                 
   19 +                                                                                                              
   20 +```                                                                                                           
   21 +com.example.reto_backend_febrero2026/                                                                         
   22 +├── familia/           # Categorias de productos ARCE                                                         
   23 +├── subfamilia/        # Subcategorias (clave compuesta)                                                      
   24 +├── licitacion/        # Licitaciones/tenders                                                                 
   25 +├── mail/              # Envio de emails y gestion de destinatarios                                           
   26 +├── notificacion/      # Log de ejecuciones (auditoria)                                                       
   27 +├── integration/                                                                                              
   28 +│   └── servlet/                                                                                              
   29 +│       ├── controller/  # Endpoints de integracion                                                           
   30 +│       ├── service/     # Cliente RSS con reintentos                                                         
   31 +│       ├── dto/         # Records para parseo XML (RSS y OCDS)                                               
   32 +│       └── strategy/    # Strategy pattern para construccion de URLs                                         
   33 +├── audit/             # @Auditable annotation + AOP aspect                                                   
   34 +└── config/            # Async, CORS, Jackson XML, MDC                                                        
   35 +```                                                                                                           
   36 +                                                                                                              
   37 +### Flujo principal                                                                                           
   38 +                                                                                                              
   39 +```                                                                                                           
   40 +Scheduler (diario a medianoche UTC-3)                                                                         
   41 +  → ArceClientService fetch RSS de ARCE                                                                       
   42 +  → Parseo XML → List<LicitacionItemRecord>                                                                   
   43 +  → LicitacionService: deduplicacion, limpieza HTML, persistencia                                             
   44 +  → EmailService: envio async de licitaciones nuevas                                                          
   45 +  → Marca licitaciones como enviadas                                                                          
   46 +```                                                                                                           
   47 +                                                                                                              
   48 +## Requisitos previos                                                                                         
   49 +                                                                                                              
   50 +- **Java 21** (JDK)                                                                                           
   51 +- **Docker** y **Docker Compose** (opcional, para base de datos local)                                        
   52 +- No se necesita Maven instalado (se usa Maven Wrapper)                                                       
   53 +                                                                                                              
   54 +## Configuracion                                                                                              
   55 +                                                                                                              
   56 +1. Copiar el archivo de ejemplo de variables de entorno:                                                      
   57 +                                                                                                              
   58 +```bash                                                                                                       
   59 +cp .env.example .env                                                                                          
   60 +```                                                                                                           
   61 +                                                                                                              
   62 +2. Editar `.env` con tus valores:                                                                             
   63 +                                                                                                              
   64 +```bash                                                                                                       
   65 +# Base de datos                                                                                               
   66 +POSTGRES_HOST=localhost                                                                                       
   67 +POSTGRES_PORT=5432                                                                                            
   68 +POSTGRES_DB=invenzis                                                                                          
   69 +POSTGRES_USER=postgres                                                                                        
   70 +POSTGRES_PASSWORD=tu_password                                                                                 
   71 +                                                                                                              
   72 +# Servidor                                                                                                    
   73 +SERVER_PORT=8080                                                                                              
   74 +CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:3000                                              
   75 +                                                                                                              
   76 +# Email (SMTP)                                                                                                
   77 +MAIL_USERNAME=tu_email@gmail.com                                                                              
   78 +MAIL_PASSWORD=tu_app_password                                                                                 
   79 +MAIL_TO=destinatario@ejemplo.com                                                                              
   80 +```                                                                                                           
   81 +                                                                                                              
   82 +> Para Gmail, necesitas generar una [contrasena de aplicacion](https://support.google.com/accounts/answer/185 
      +833).                                                                                                         
   83 +                                                                                                              
   84 +## Ejecucion                                                                                                  
   85 +                                                                                                              
   86 +### Desarrollo local                                                                                          
   87 +                                                                                                              
   88 +```bash                                                                                                       
   89 +# Levantar solo PostgreSQL con Docker                                                                         
   90 +docker compose up -d postgres                                                                                 
   91 +                                                                                                              
   92 +# Iniciar la aplicacion                                                                                       
   93 +./mvnw spring-boot:run                                                                                        
   94 +```                                                                                                           
   95 +                                                                                                              
   96 +### Con Docker (aplicacion completa)                                                                          
   97 +                                                                                                              
   98 +```bash                                                                                                       
   99 +docker compose up -d                                                                                          
  100 +```                                                                                                           
  101 +                                                                                                              
  102 +### Compilar y empaquetar                                                                                     
  103 +                                                                                                              
  104 +```bash                                                                                                       
  105 +# Compilar (genera mappers de MapStruct)                                                                      
  106 +./mvnw compile                                                                                                
  107 +                                                                                                              
  108 +# Ejecutar tests                                                                                              
  109 +./mvnw test                                                                                                   
  110 +                                                                                                              
  111 +# Ejecutar un test especifico                                                                                 
  112 +./mvnw test -Dtest=LicitacionServiceTest                                                                      
  113 +                                                                                                              
  114 +# Generar JAR sin ejecutar tests                                                                              
  115 +./mvnw package -DskipTests                                                                                    
  116 +```                                                                                                           
  117 +                                                                                                              
  118 +## API Endpoints                                                                                              
  119 +                                                                                                              
  120 +La documentacion interactiva esta disponible en Swagger UI una vez iniciada la aplicacion:                    
  121 +                                                                                                              
  122 +- **Swagger UI:** http://localhost:8080/swagger-ui.html                                                       
  123 +- **OpenAPI JSON:** http://localhost:8080/reto/v3/api-docs                                                    
  124 +                                                                                                              
  125 +### Resumen de endpoints                                                                                      
  126 +                                                                                                              
  127 +| Metodo | Ruta | Descripcion |                                                                               
  128 +|--------|------|-------------|                                                                               
  129 +| **Familias** | | |                                                                                          
  130 +| GET | `/familias` | Listar todas las categorias |                                                           
  131 +| GET | `/familia/{cod}` | Obtener categoria por codigo |                                                     
  132 +| **Subfamilias** | | |                                                                                       
  133 +| GET | `/subfamilias` | Listar todas las subcategorias |                                                     
  134 +| GET | `/subfamilias/familia/{famiCod}` | Filtrar por categoria padre |                                      
  135 +| **Licitaciones** | | |                                                                                      
  136 +| GET | `/licitaciones/{id}` | Obtener licitacion por ID |                                                    
  137 +| GET | `/licitacion/titulo/{titulo}` | Buscar por titulo |                                                   
  138 +| GET | `/familias/{familiaCod}/subfamilia/{subfamiliaCod}` | Filtrar por categoria/subcategoria |            
  139 +| **Email** | | |                                                                                             
  140 +| GET | `/email` | Listar destinatarios activos |                                                             
  141 +| POST | `/email` | Agregar destinatario |                                                                    
  142 +| PUT | `/email/{address}` | Activar/desactivar destinatario |                                                
  143 +| DELETE | `/email/{address}` | Eliminar destinatario |                                                       
  144 +| **Notificaciones** | | |                                                                                    
  145 +| GET | `/notificaciones` | Listar logs de ejecucion |                                                        
  146 +| GET | `/notificaciones/{id}` | Detalle de una ejecucion |                                                   
  147 +                                                                                                              
  148 +## Base de datos                                                                                              
  149 +                                                                                                              
  150 +El esquema se gestiona con Flyway (migraciones en `src/main/resources/db/migration/`):                        
  151 +                                                                                                              
  152 +- **familias** - Categorias de productos ARCE (cod PK)                                                        
  153 +- **subfamilias** - Subcategorias con clave compuesta (fami_cod + cod)                                        
  154 +- **licitacion** - Licitaciones con FK a familia y subfamilia                                                 
  155 +- **destinos_email** - Lista de distribucion de emails                                                        
  156 +- **notificacion** - Log de ejecuciones del scheduler                                                         
  157 +                                                                                                              
  158 +Los datos semilla (10 familias, 37 subfamilias) se cargan automaticamente con `V2_insert_data.sql`.           
  159 +                                                                                                              
  160 +## Integracion con ARCE                                                                                       
  161 +                                                                                                              
  162 +El sistema consume el feed RSS de compras estatales de Uruguay:                                               
  163 +                                                                                                              
  164 +```                                                                                                           
  165 +https://www.comprasestatales.gub.uy/consultas/rss/tipo-pub/VIG/tipo-doc/C/filtro-cat/CAT/familia/{cod}/sub-fa 
      +milia/{cod}                                                                                                   
  166 +```                                                                                                           
  167 +                                                                                                              
  168 +### Mecanismo de reintentos                                                                                   
  169 +                                                                                                              
  170 +El cliente RSS usa `@Retryable` con backoff exponencial:                                                      
  171 +- 5 intentos maximos                                                                                          
  172 +- Delay inicial: 2 segundos                                                                                   
  173 +- Multiplicador: 3x (2s → 6s → 18s → 54s → 162s)                                                              
  174 +                                                                                                              
  175 +## Tests                                                                                                      
  176 +                                                                                                              
  177 +```bash                                                                                                       
  178 +# Ejecutar todos los tests                                                                                    
  179 +./mvnw test                                                                                                   
  180 +                                                                                                              
  181 +# Tests disponibles:                                                                                          
  182 +# - FamiliaControllerTest, FamiliaServiceTest                                                                 
  183 +# - SubfamiliaControllerTest, SubfamiliaServiceTest                                                           
  184 +# - LicitacionControllerTest, LicitacionServiceTest                                                           
  185 +# - ArceRssStrategyTest                                                                                       
  186 +```                                                                                                           
  187 +                                                                                                              
  188 +Los tests usan **JUnit 5** con **Mockito** y base de datos **H2** en memoria.                                 
  189 +                                                                                                              
  190 +## Deploy                                                                                                     
  191 +                                                                                                              
  192 +El proyecto esta configurado para deploy en **Google Cloud Run**:                                             
  193 +                                                                                                              
  194 +- **GitHub Actions** (`.github/workflows/deploy-dev.yml`): CI/CD en push a rama `dev`                         
  195 +- **Cloud Build** (`cloudbuild.yml`): Build y deploy del contenedor Docker                                    
  196 +- **Dockerfile**: Build multi-stage (Maven build + JRE runtime Alpine)                                        
  197 +                                                                                                              
  198 +## Documentacion                                                                                               
  199 +                                                                                                              
  200 +La documentacion se genera con **Swagger** y se puede acceder a ella en:                                        
  201 +                                                                                                              
  202 +```                                                                                                            
  203 +http://localhost:8080/swagger-ui/index.html                                                                    
  204 +```                                                                                                            