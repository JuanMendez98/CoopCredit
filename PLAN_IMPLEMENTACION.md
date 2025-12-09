# Plan de Implementación - Sistema CoopCredit

## Estrategia General
- **Enfoque**: Incremental y compilable en cada fase
- **Principio**: KISS (Keep It Simple, Stupid)
- **Validación**: Compilar y probar después de cada fase
- **Duración estimada**: 10-14 horas de desarrollo enfocado

---

## Dependencias Globales

### 📦 Desde Spring Initializr (https://start.spring.io/)

**Configuración Base:**
- **Project**: Maven
- **Language**: Java
- **Spring Boot**: 3.2.x (última stable)
- **Java**: 17 o 21
- **Packaging**: Jar

**Dependencias para `credit-application-service`:**
```
✓ Spring Web
✓ Spring Data JPA
✓ Spring Security
✓ Validation (Bean Validation con Hibernate Validator)
✓ Flyway Migration
✓ PostgreSQL Driver
✓ Lombok
✓ Spring Boot Actuator
✓ Spring Boot DevTools (opcional)
```

**Dependencias para `risk-central-mock-service`:**
```
✓ Spring Web
✓ Lombok
✓ Spring Boot DevTools (opcional)
```

### 📦 Desde Maven Repository (https://mvnrepository.com/)

**Para `credit-application-service`:**

1. **JWT (JJWT)**
   ```xml
   <dependency>
       <groupId>io.jsonwebtoken</groupId>
       <artifactId>jjwt-api</artifactId>
       <version>0.12.6</version>
   </dependency>
   <dependency>
       <groupId>io.jsonwebtoken</groupId>
       <artifactId>jjwt-impl</artifactId>
       <version>0.12.6</version>
       <scope>runtime</scope>
   </dependency>
   <dependency>
       <groupId>io.jsonwebtoken</groupId>
       <artifactId>jjwt-jackson</artifactId>
       <version>0.12.6</version>
       <scope>runtime</scope>
   </dependency>
   ```

2. **MapStruct (Opcional - puede omitirse al inicio)**
   ```xml
   <dependency>
       <groupId>org.mapstruct</groupId>
       <artifactId>mapstruct</artifactId>
       <version>1.6.3</version>
   </dependency>
   <dependency>
       <groupId>org.mapstruct</groupId>
       <artifactId>mapstruct-processor</artifactId>
       <version>1.6.3</version>
       <scope>provided</scope>
   </dependency>
   ```

3. **Testcontainers (para FASE 5)**
   ```xml
   <dependency>
       <groupId>org.testcontainers</groupId>
       <artifactId>testcontainers</artifactId>
       <version>1.21.3</version>
       <scope>test</scope>
   </dependency>
   <dependency>
       <groupId>org.testcontainers</groupId>
       <artifactId>postgresql</artifactId>
       <version>1.21.3</version>
       <scope>test</scope>
   </dependency>
   <dependency>
       <groupId>org.testcontainers</groupId>
       <artifactId>junit-jupiter</artifactId>
       <version>1.21.3</version>
       <scope>test</scope>
   </dependency>
   ```

4. **Problem Detail / RFC 7807 (Spring Boot 3 lo incluye nativamente)**
   - No necesita dependencia adicional desde Spring Boot 3.0+

**Para `risk-central-mock-service`:**
- No requiere dependencias adicionales de Maven Repository

---

## FASE 0: Setup Inicial y Microservicio Mock
**Duración estimada**: 1-2 horas
**Estado**: ✅ COMPLETADA

### Objetivos
✅ Crear estructura de proyectos
✅ Implementar `risk-central-mock-service` completamente funcional
✅ Validar que el mock responde correctamente

### Tareas

#### 0.1 Crear proyectos base
- [x] Crear directorio raíz `CoopCredit/`
- [x] Generar `risk-central-mock-service` desde Spring Initializr (solo Spring Web + Lombok)
- [x] Generar `credit-application-service` desde Spring Initializr (todas las dependencias listadas)
- [x] Agregar dependencias de Maven Repository en cada `pom.xml`
- [x] Verificar compilación: `mvn clean compile` en ambos proyectos

#### 0.2 Implementar risk-central-mock-service
- [x] Crear paquete: `com.crudzaso.riskapp`
- [x] Crear DTO: `RiskEvaluationRequest` (document, amount, term)
- [x] Crear DTO: `RiskEvaluationResponse` (document, score, riskLevel, detail)
- [x] Crear Enum: `RiskLevel` (HIGH, MEDIUM, LOW)
- [x] Crear `RiskEvaluationController` con endpoint `POST /api/risk/risk-evaluation`
- [x] Implementar lógica de generación de score consistente:
  - Hash del documento → seed
  - Score entre 300-950 basado en seed
  - Clasificación según score
- [x] Configurar `application.yml`:
  - Puerto: 8081
  - Logging básico

#### 0.3 Probar risk-central-mock-service
- [x] Ejecutar: `mvn spring-boot:run`
- [x] Probar con cURL/Postman: `POST http://localhost:8081/api/risk/risk-evaluation`
- [x] Verificar mismo documento → mismo score
- [x] Verificar documento diferente → score diferente

**✅ Criterio de aceptación FASE 0:**
- ✅ Mock service responde correctamente
- ✅ Scores consistentes por documento
- ✅ Proyecto compila sin errores
- ✅ Commit realizado: `feat(risk-central-mock-service): implement risk evaluation endpoint`

---

## FASE 1: Core del Sistema - CRUD Básico (SIN Seguridad)
**Duración estimada**: 3-4 horas
**Estado**: ✅ COMPLETADA

### Objetivos
✅ Establecer arquitectura hexagonal básica
✅ Implementar entidades JPA con relaciones
✅ CRUD de Afiliados y Solicitudes
✅ Migraciones con Flyway
✅ Endpoints REST funcionando
✅ Error handling básico con ProblemDetail
✅ Logging estructurado

### Tareas

#### 1.1 Configurar base de datos y Flyway
- [x] Configurar `application.yml`:
  - PostgreSQL en local (puerto 5444)
  - Configuración JPA/Hibernate
  - Flyway habilitado
- [x] Crear migración `V1__create_schema.sql`:
  - Tabla `affiliates` con columnas: id, document, name, salary, affiliation_date, status
  - Tabla `credit_requests` con columnas: id, affiliate_id, amount, term, rate, request_date, status
- [x] Crear migración `V2__create_evaluations.sql`:
  - Tabla `risk_evaluations` con columnas: id, credit_request_id, score, risk_level, decision, reason, evaluation_date
- [x] Ejecutar y verificar: compilado y funcional

#### 1.2 Establecer estructura hexagonal
- [x] Crear estructura de paquetes:
  ```
  com.crudzao.creditapp/
  ├── domain/
  │   └── enums/          (AffiliateStatus, CreditRequestStatus, CreditDecision, RiskLevel)
  ├── application/
  │   └── services/       (AffiliateService, CreditRequestService)
  └── infrastructure/
      ├── adapters/
      │   ├── rest/       (AffiliateController, CreditRequestController, DTOs)
      │   └── jpa/        (AffiliateEntity, CreditRequestEntity, RiskEvaluationEntity, Repositories)
      ├── exception/      (ResourceNotFoundException, BusinessException, InvalidStateException)
      └── config/         (GlobalExceptionHandler)
  ```

#### 1.3 Dominio - Entidades JPA
- [x] Crear entidad `AffiliateEntity`:
  - Mapeo JPA completo con @Entity, @Table
  - Validaciones con @NotNull, @NotBlank, @Positive
  - Relación OneToMany con CreditRequestEntity
- [x] Crear entidad `CreditRequestEntity`:
  - Mapeo JPA completo con @Entity, @Table
  - Relación ManyToOne con AffiliateEntity (FetchType.LAZY)
  - Relación OneToOne con RiskEvaluationEntity
- [x] Crear entidad `RiskEvaluationEntity`:
  - Mapeo JPA completo
  - Relación OneToOne con CreditRequestEntity
- [x] Crear Enums: `AffiliateStatus`, `CreditRequestStatus`, `RiskLevel`, `CreditDecision`

#### 1.4 Repositorios JPA
- [x] Crear `AffiliateRepository extends JpaRepository<AffiliateEntity, Long>`
  - Query custom: `Optional<AffiliateEntity> findByDocument(String document)`
- [x] Crear `CreditRequestRepository extends JpaRepository<CreditRequestEntity, Long>`
  - Query custom: `List<CreditRequestEntity> findByAffiliateId(Long affiliateId)`
  - Query custom: `List<CreditRequestEntity> findByStatus(CreditRequestStatus status)`
- [x] Crear `RiskEvaluationRepository extends JpaRepository<RiskEvaluationEntity, Long>`

#### 1.5 DTOs y Mappers
- [x] Crear DTOs de request/response para Affiliate:
  - `AffiliateRequest`, `AffiliateResponse`
- [x] Crear DTOs de request/response para CreditRequest:
  - `CreditRequestRequest`, `CreditRequestResponse`
- [x] Mappers manuales implementados en servicios

#### 1.6 Servicios - Casos de Uso
- [x] Implementar `AffiliateService`:
  - Registrar afiliado con validación de documento único
  - Obtener por ID
  - Obtener por documento
  - Validación de estado ACTIVE
- [x] Implementar `CreditRequestService`:
  - Crear solicitud en estado PENDING
  - Obtener solicitudes por afiliado
  - Obtener solicitudes por status
  - Validación de afiliado activo

#### 1.7 Adaptadores REST - Controllers
- [x] Crear `AffiliateController`:
  - `POST /api/affiliates` → registrar (201 Created)
  - `GET /api/affiliates/{id}` → obtener
  - `GET /api/affiliates/document/{document}` → buscar por documento
- [x] Crear `CreditRequestController`:
  - `POST /api/credit-requests` → crear solicitud (201 Created)
  - `GET /api/credit-requests/{id}` → obtener
  - `GET /api/credit-requests/affiliate/{affiliateId}` → listar por afiliado
  - `GET /api/credit-requests/status/{status}` → listar por status
- [x] Validación con @Valid en todos los endpoints

#### 1.8 Error Handling y Logging
- [x] Crear excepciones custom:
  - `ResourceNotFoundException` (404)
  - `BusinessException` (400)
  - `InvalidStateException` (409)
- [x] Implementar `GlobalExceptionHandler` con @ControllerAdvice
  - Retorna ProblemDetail (RFC 7807)
  - Incluye timestamp, status, detail, instance, type, title
- [x] Agregar logging con @Slf4j:
  - info: operaciones principales
  - warn: violaciones de reglas de negocio
  - debug: consultas detalladas
  - error: excepciones no esperadas

#### 1.9 Pruebas FASE 1
- [x] Ejecutar: `mvn clean compile` (sin errores)
- [x] Validar endpoints lógicamente
- [x] Verificar relaciones JPA en entidades
- [x] Commit: `feat(credit-application-service): implement FASE 1`

**✅ Criterio de aceptación FASE 1:**
- ✅ CRUD completo funciona
- ✅ Relaciones JPA correctas (1-N, N-1, 1-1)
- ✅ Error handling con ProblemDetail
- ✅ Logging estructurado
- ✅ Compilación exitosa
- ✅ Commit pusheado a rama dev

---

## FASE 2: Integración y Evaluación de Riesgo
**Duración estimada**: 2-3 horas
**Estado**: ✅ COMPLETADA

### Objetivos
✓ Conectar con risk-central-mock-service
✓ Implementar caso de uso "Evaluar Solicitud"
✓ Aplicar políticas de crédito
✓ Proceso transaccional completo

### Tareas

#### 2.1 Cliente REST para risk-central-mock-service
- [x] Crear `RiskCentralClient` (usando `RestTemplate` o `WebClient`)
- [x] Crear DTOs para comunicación: `RiskEvaluationRequestDTO`, `RiskEvaluationResponseDTO`
- [x] Configurar URL del servicio en `application.yml`:
  ```yaml
  risk:
    service:
      url: http://localhost:8081/api/risk/risk-evaluation
  ```
- [x] Implementar método `evaluateRisk(String documento, double monto, int plazo)`
- [x] Manejo básico de errores de comunicación

#### 2.2 Puerto de salida para Risk Central
- [x] Crear interfaz `RiskCentralPort` en `application/ports/out`
- [x] Implementar `RiskCentralAdapter` que usa `RiskCentralClient`
- [x] Registrar como @Component

#### 2.3 Políticas de Crédito
- [x] Crear clase `CreditPolicy` con reglas de negocio:
  - Relación cuota/ingreso (ejemplo: cuota <= 40% salario)
  - Monto máximo según salario (ejemplo: monto <= 5x salario)
  - Antigüedad mínima (ejemplo: 6 meses desde afiliación)
  - Evaluación según score/nivel de riesgo
- [x] Crear métodos de validación que retornan `ValidationResult` (válido + motivos)

#### 2.4 Caso de Uso: Evaluar Solicitud
- [x] Crear puerto de entrada: `EvaluarSolicitudUseCase`
- [x] Implementar `EvaluacionSolicitudService`:
  - Obtener solicitud (debe estar en PENDIENTE)
  - Obtener afiliado asociado
  - Validar estado del afiliado (ACTIVO)
  - Consultar risk-central-mock-service (via RiskCentralPort)
  - Aplicar políticas de crédito (CreditPolicy)
  - Crear EvaluacionRiesgo con resultado
  - Actualizar estado de solicitud (APROBADO/RECHAZADO)
  - Guardar evaluación y solicitud
  - Todo en una transacción @Transactional

#### 2.5 Controller para Evaluación
- [x] Agregar endpoint en `SolicitudController`:
  - `POST /api/solicitudes/{id}/evaluar` → ejecuta evaluación
  - Retorna `EvaluacionRiesgoResponse` con resultado

#### 2.6 Optimización de consultas JPA
- [x] Revisar N+1 en carga de Afiliado + Solicitudes
- [x] Usar `@EntityGraph` o `JOIN FETCH` si es necesario
- [x] Configurar `hibernate.default_batch_fetch_size` en `application.yml`

#### 2.7 Pruebas FASE 2
- [x] Arrancar ambos servicios (risk-central en 8081, credit-application en 8080)
- [x] Probar flujo completo:
  1. Crear afiliado
  2. Crear solicitud
  3. Evaluar solicitud → verificar APROBADO/RECHAZADO
  4. Verificar que mismo documento da mismo score
- [x] Verificar logs de transacción
- [x] Revisar base de datos: tabla evaluaciones_riesgo con datos

**✅ Criterio de aceptación FASE 2:**
- ✅ Integración con risk-central funciona
- ✅ Evaluación completa y transaccional
- ✅ Políticas de crédito aplicadas correctamente
- ✅ Estado de solicitud actualizado

---


## FASE 3: Seguridad JWT
**Duración estimada**: 2-3 horas
**Estado**: ✅ COMPLETADA

### Objetivos
✓ Implementar autenticación con JWT
✓ Roles: ROLE_AFFILIATE, ROLE_ANALYST, ROLE_ADMIN
✓ Control de acceso por endpoint
✓ Registro y login

### Tareas

#### 3.1 Entidad de Usuario y Roles
- [x] Crear migración Flyway `V3__create_users_roles.sql`:
  - Tabla `users` (id, username, password, email, enabled)
  - Tabla `roles` (id, name)
  - Tabla `user_roles` (user_id, role_id) - relación ManyToMany
- [x] Crear entidad `UserEntity`:
  - Relación ManyToMany con RoleEntity
  - Implementar `UserDetails` de Spring Security
- [x] Crear entidad `RoleEntity`
- [x] Crear `UserRepository`, `RoleRepository`

#### 3.2 Servicio de Usuario
- [x] Crear `UserDetailsService` personalizado: `CustomUserDetailsService`
  - Implementar `loadUserByUsername(String username)`
- [x] Crear `AuthenticationService`:
  - `register(RegisterRequest request)` → crear usuario + roles
  - `authenticate(LoginRequest request)` → validar credenciales
  - Usar `PasswordEncoder` (BCrypt)

#### 3.3 JWT Utilities
- [x] Crear `JwtTokenProvider`:
  - `generateToken(Authentication auth)` → crear JWT
  - `getUsernameFromToken(String token)` → extraer username
  - `validateToken(String token)` → validar JWT
  - Configurar secret y expiration en `application.yaml`
- [x] Configurar clave secreta y tiempo de expiración (24h)

#### 3.4 Filtro JWT
- [x] Crear `JwtAuthenticationFilter extends OncePerRequestFilter`:
  - Extraer token del header `Authorization: Bearer {token}`
  - Validar token
  - Establecer `Authentication` en `SecurityContextHolder`
- [x] Registrar filtro en cadena de seguridad

#### 3.5 Configuración de Spring Security
- [x] Crear `SecurityConfig`:
  - Configurar `SecurityFilterChain`
  - Desactivar CSRF (API REST stateless)
  - Configurar endpoints públicos: `/api/auth/**`
  - Configurar endpoints protegidos:
    - `/api/affiliates/**` → ROLE_AFFILIATE, ROLE_ADMIN
    - `/api/credit-requests/*/evaluate` → ROLE_ANALYST, ROLE_ADMIN
    - `/api/credit-requests/**` → authenticated
  - Agregar `JwtAuthenticationFilter` antes de `UsernamePasswordAuthenticationFilter`
  - Session management: STATELESS

#### 3.6 Controller de Autenticación
- [x] Crear `AuthController`:
  - `POST /api/auth/register` → registrar usuario
  - `POST /api/auth/login` → login y retornar JWT
- [x] Crear DTOs: `RegisterRequest`, `LoginRequest`, `AuthResponse` (con token)

#### 3.7 Control de acceso por recurso
- [x] Protección de endpoints por roles
  - Afiliado accede a sus solicitudes
  - Analista puede evaluar solicitudes
  - Admin acceso completo
- [x] Validación con JWT en SecurityContext

#### 3.8 Datos iniciales
- [x] Roles creados en migración V3:
  - ROLE_ADMIN
  - ROLE_ANALYST
  - ROLE_AFFILIATE

#### 3.9 Pruebas FASE 3
- [x] Ejecutar: `mvn clean compile` sin errores
- [x] Compilación exitosa
- [x] Registrar usuario nuevo - funciona
- [x] Login - obtener JWT válido
- [x] Usar JWT en header - acceso permitido
- [x] Sin JWT - 401 Unauthorized
- [x] Crear afiliado con JWT - funciona
- [x] Crear solicitud con JWT - funciona
- [x] Evaluar solicitud - APPROVED/REJECTED con JWT

**✅ Criterio de aceptación FASE 3:**
- ✅ Registro y login funcionan correctamente
- ✅ JWT generado y validado correctamente
- ✅ Control de acceso por roles funciona
- ✅ Endpoints protegidos correctamente
- ✅ Flujo completo: login → create affiliate → create request → evaluate

---

## FASE 4: Validaciones y Manejo de Errores
**Duración estimada**: 1-2 horas

### Objetivos
✓ Validaciones avanzadas con Bean Validation
✓ Manejo global de errores con @ControllerAdvice
✓ Formato ProblemDetail (RFC 7807)
✓ Logging estructurado

### Tareas

#### 4.1 Validaciones con Bean Validation
- [ ] Agregar anotaciones de validación en DTOs:
  - `@NotNull`, `@NotBlank`, `@Positive`, `@Min`, `@Max`, `@Email`
  - Custom validation: `@ValidCreditRequest` (validación cruzada cuota/ingreso)
- [ ] Crear validadores personalizados:
  - `@ValidAfiliado` → estado ACTIVO
  - `@ValidSolicitud` → plazo razonable, monto coherente
- [ ] Implementar clases `ConstraintValidator`

#### 4.2 Excepciones de dominio
- [ ] Crear jerarquía de excepciones:
  - `DomainException` (base)
  - `AfiliadoNotFoundException`
  - `SolicitudNotFoundException`
  - `AfiliadoInactivoException`
  - `SolicitudYaEvaluadaException`
  - `ValidationException`
  - `RiskServiceException`
- [ ] Lanzar excepciones apropiadas en servicios

#### 4.3 Global Exception Handler
- [ ] Crear `GlobalExceptionHandler` con `@ControllerAdvice`:
  - `handleMethodArgumentNotValid` → errores de validación
  - `handleNotFoundException` → 404
  - `handleAccessDeniedException` → 403
  - `handleAuthenticationException` → 401
  - `handleValidationException` → 400
  - `handleRiskServiceException` → 503 Service Unavailable
  - `handleGenericException` → 500
- [ ] Retornar `ProblemDetail` (RFC 7807) con:
  - `type` (URI del tipo de error)
  - `title` (título legible)
  - `status` (código HTTP)
  - `detail` (mensaje detallado)
  - `instance` (path del request)
  - Campos custom: `timestamp`, `traceId` (de MDC)

#### 4.4 Logging estructurado
- [ ] Configurar Logback/SLF4J en `application.yml`:
  - Nivel INFO en general
  - Nivel DEBUG para paquetes propios
  - Logging de SQL en desarrollo (opcional)
- [ ] Usar MDC (Mapped Diagnostic Context) para traceId:
  - Crear filtro que agregue traceId a MDC
  - Incluir traceId en logs y responses
- [ ] Agregar logs en puntos clave:
  - Inicio/fin de casos de uso
  - Llamadas a servicios externos
  - Errores capturados

#### 4.5 Configuración de mensajes de error
- [ ] Crear `messages.properties` con mensajes de error personalizados
- [ ] Internacionalización (opcional): `messages_es.properties`, `messages_en.properties`

#### 4.6 Pruebas FASE 4
- [ ] Probar validaciones:
  - Enviar request con datos inválidos → 400 con ProblemDetail
- [ ] Probar errores de negocio:
  - Solicitud con afiliado inactivo → 400 con mensaje claro
  - Solicitud ya evaluada → 409 Conflict
- [ ] Probar errores de autenticación/autorización → 401/403 con ProblemDetail
- [ ] Verificar logs: traceId presente, mensajes estructurados

**✅ Criterio de aceptación FASE 4:**
- Validaciones funcionan correctamente
- Errores retornan ProblemDetail
- Logs estructurados con traceId
- Mensajes de error claros y útiles

---

## FASE 5: Tests y Observabilidad
**Duración estimada**: 2-3 horas

### Objetivos
✓ Pruebas unitarias con JUnit + Mockito
✓ Pruebas de integración con MockMvc
✓ Testcontainers para base de datos
✓ Actuator + Micrometer para métricas

### Tareas

#### 5.1 Tests Unitarios - Casos de Uso
- [ ] Crear tests para `CreditPolicy`:
  - Política de cuota/ingreso
  - Política de monto máximo
  - Política de antigüedad
- [ ] Crear tests para `EvaluacionSolicitudService`:
  - Mock del `RiskCentralPort`
  - Mock de repositorios
  - Verificar lógica de aprobación/rechazo
  - Verificar transaccionalidad (rollback en error)
- [ ] Usar `@ExtendWith(MockitoExtension.class)`, `@Mock`, `@InjectMocks`

#### 5.2 Tests de Integración - Controllers
- [ ] Configurar `@SpringBootTest` + `@AutoConfigureMockMvc`
- [ ] Tests para `AuthController`:
  - Registro exitoso
  - Login exitoso → retorna JWT
  - Login fallido → 401
- [ ] Tests para `AfiliadoController`:
  - Crear afiliado con autenticación
  - Acceso denegado sin JWT
- [ ] Tests para `SolicitudController`:
  - Crear solicitud
  - Evaluar solicitud con mock del RiskCentralPort
  - Verificar control de acceso por roles

#### 5.3 Testcontainers para PostgreSQL
- [ ] Configurar `@Testcontainers` en clase base de tests de integración
- [ ] Crear contenedor PostgreSQL:
  ```java
  @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");
  ```
- [ ] Configurar datasource dinámica en `@DynamicPropertySource`
- [ ] Ejecutar tests contra base de datos real en contenedor

#### 5.4 Actuator - Configuración
- [ ] Configurar `application.yml`:
  ```yaml
  management:
    endpoints:
      web:
        exposure:
          include: health,info,metrics,prometheus
    endpoint:
      health:
        show-details: always
    metrics:
      tags:
        application: credit-application-service
  ```
- [ ] Crear `info.properties` con información de la aplicación

#### 5.5 Métricas Personalizadas con Micrometer
- [ ] Crear métricas custom:
  - Contador: solicitudes creadas
  - Contador: solicitudes aprobadas/rechazadas
  - Timer: tiempo de evaluación de solicitud
  - Contador: fallas de autenticación
  - Contador: llamadas a risk-central (éxito/error)
- [ ] Inyectar `MeterRegistry` en servicios
- [ ] Registrar métricas en puntos clave del código

#### 5.6 Health Indicators Personalizados
- [ ] Crear `RiskCentralHealthIndicator`:
  - Verificar conectividad con risk-central-mock-service
  - Retornar UP/DOWN con detalles
- [ ] Implementar `HealthIndicator` de Spring Boot Actuator

#### 5.7 Pruebas FASE 5
- [ ] Ejecutar todos los tests: `mvn clean test`
- [ ] Verificar cobertura con JaCoCo (opcional): `mvn clean verify`
- [ ] Acceder a endpoints de Actuator:
  - `GET /actuator/health` → estado UP con detalles
  - `GET /actuator/metrics` → lista de métricas
  - `GET /actuator/metrics/solicitudes.creadas` → valor de métrica custom
  - `GET /actuator/prometheus` → formato para Prometheus (opcional)
- [ ] Verificar health indicator de risk-central

**✅ Criterio de aceptación FASE 5:**
- Tests unitarios pasan
- Tests de integración con Testcontainers pasan
- Actuator expone métricas correctamente
- Health indicators funcionan

---

## FASE 6: Contenerización y Documentación
**Duración estimada**: 1-2 horas

### Objetivos
✓ Dockerfiles multi-stage para ambos servicios
✓ Docker Compose funcional
✓ README completo
✓ Documentación de API (Swagger opcional)

### Tareas

#### 6.1 Dockerfile para credit-application-service
- [ ] Crear `Dockerfile` multi-stage:
  - **Stage 1 (build)**:
    - Imagen: `maven:3.9-eclipse-temurin-17-alpine`
    - Copiar `pom.xml` y descargar dependencias
    - Copiar código fuente
    - Ejecutar `mvn clean package -DskipTests`
  - **Stage 2 (run)**:
    - Imagen: `eclipse-temurin:17-jre-alpine`
    - Copiar JAR desde stage build
    - Exponer puerto 8080
    - ENTRYPOINT: `java -jar app.jar`
- [ ] Optimizar capas de Docker para cache
- [ ] Probar build: `docker build -t credit-application-service .`

#### 6.2 Dockerfile para risk-central-mock-service
- [ ] Crear `Dockerfile` multi-stage similar
- [ ] Exponer puerto 8081
- [ ] Probar build: `docker build -t risk-central-mock-service .`

#### 6.3 Docker Compose
- [ ] Crear `docker-compose.yml` con servicios:
  - **postgres**:
    - Imagen: `postgres:15-alpine`
    - Variables de entorno: POSTGRES_DB, POSTGRES_USER, POSTGRES_PASSWORD
    - Volumen persistente
    - Puerto: 5432
  - **risk-central-mock-service**:
    - Build desde Dockerfile
    - Puerto: 8081
    - Variables de entorno si es necesario
  - **credit-application-service**:
    - Build desde Dockerfile
    - Puerto: 8080
    - Variables de entorno: DB_HOST, DB_PORT, RISK_SERVICE_URL
    - Depende de: postgres, risk-central-mock-service
    - Health check (opcional)
- [ ] Configurar red para comunicación entre servicios
- [ ] Probar: `docker-compose up -d`
- [ ] Verificar logs: `docker-compose logs -f`

#### 6.4 Swagger/OpenAPI (opcional pero recomendado)
- [ ] Agregar dependencia `springdoc-openapi-starter-webmvc-ui`
- [ ] Configurar `application.yml`:
  ```yaml
  springdoc:
    api-docs:
      path: /api-docs
    swagger-ui:
      path: /swagger-ui.html
  ```
- [ ] Agregar anotaciones `@Operation`, `@ApiResponse` en controllers
- [ ] Acceder a: `http://localhost:8080/swagger-ui.html`

#### 6.5 Colección de Postman
- [ ] Crear workspace en Postman
- [ ] Crear colección con todos los endpoints:
  - Auth: register, login
  - Afiliados: crear, obtener
  - Solicitudes: crear, listar, evaluar
  - Risk Central: evaluar riesgo
- [ ] Exportar colección como JSON
- [ ] Incluir variables de entorno ({{baseUrl}}, {{token}})

#### 6.6 README Principal
- [ ] Crear `README.md` completo con:
  - **Descripción del sistema**: CoopCredit y el problema que resuelve
  - **Arquitectura**: diagrama de componentes (arquitectura hexagonal + microservicios)
  - **Tecnologías utilizadas**: Java 17, Spring Boot 3, PostgreSQL, Docker, JWT, etc.
  - **Requisitos previos**: Java, Maven, Docker
  - **Estructura del proyecto**: árbol de directorios
  - **Configuración**:
    - Variables de entorno
    - Perfiles (dev, prod)
  - **Ejecución local**:
    - Con Maven: `mvn spring-boot:run`
    - Con Docker Compose: `docker-compose up`
  - **Endpoints principales**:
    - Tabla con método, path, descripción, roles
  - **Roles y permisos**: tabla con roles y accesos
  - **Flujo de evaluación de crédito**: diagrama de secuencia o pasos
  - **Pruebas**:
    - Ejecutar tests: `mvn test`
    - Coverage (si aplica)
  - **Monitoreo**:
    - Actuator endpoints
    - Métricas disponibles
  - **Capturas de pantalla**:
    - Swagger UI
    - Postman collection
    - Actuator metrics
    - Logs de aplicación
  - **Mejoras futuras** (opcional)
  - **Autor y contacto**

#### 6.7 Diagramas
- [ ] Crear diagrama de arquitectura hexagonal:
  - Capas: Dominio, Aplicación, Infraestructura
  - Puertos y Adaptadores
  - Flujo de dependencias
- [ ] Crear diagrama de arquitectura de microservicios:
  - credit-application-service
  - risk-central-mock-service
  - Base de datos
  - Flujo de comunicación
- [ ] Crear diagrama de casos de uso:
  - Actores: Afiliado, Analista, Admin
  - Casos de uso principales
- [ ] Herramientas sugeridas: draw.io, PlantUML, Mermaid

#### 6.8 README de cada microservicio
- [ ] Crear `README.md` en cada proyecto con:
  - Descripción específica del servicio
  - Endpoints
  - Configuración
  - Cómo ejecutar

#### 6.9 Pruebas finales
- [ ] Ejecutar todo desde cero con Docker Compose:
  1. `docker-compose down -v` (limpiar)
  2. `docker-compose build`
  3. `docker-compose up`
  4. Esperar que servicios estén UP
  5. Probar flujo completo con Postman
- [ ] Verificar persistencia de datos
- [ ] Verificar métricas en Actuator
- [ ] Verificar logs en consola

**✅ Criterio de aceptación FASE 6:**
- Docker Compose levanta todos los servicios
- Sistema funciona end-to-end en contenedores
- README completo y claro
- Swagger/Postman collection disponible
- Diagramas presentes

---

## Entregables Finales

### ✅ Repositorio GitHub
- [ ] Crear repositorio público en GitHub
- [ ] Subir código con estructura clara
- [ ] Incluir `.gitignore` apropiado
- [ ] README.md en raíz del repositorio
- [ ] Colección Postman en directorio `/postman`
- [ ] Diagramas en directorio `/docs`

### ✅ Documentación
- [ ] README principal completo
- [ ] README por microservicio
- [ ] Diagramas de arquitectura
- [ ] Instrucciones de ejecución
- [ ] Capturas de pantalla o GIFs demostrativos

### ✅ Código
- [ ] Arquitectura hexagonal implementada
- [ ] Seguridad JWT completa
- [ ] Validaciones y manejo de errores
- [ ] Tests (unitarios, integración, Testcontainers)
- [ ] Actuator y métricas
- [ ] Dockerfiles y docker-compose

### ✅ Extras opcionales (si hay tiempo)
- [ ] CI/CD con GitHub Actions (build + tests)
- [ ] Configuración centralizada con Config Server
- [ ] API Gateway (Spring Cloud Gateway)
- [ ] Prometheus + Grafana para visualización de métricas
- [ ] ELK Stack para logs centralizados

---

## Recomendaciones Finales

### 🎯 Priorización
Si el tiempo es limitado, asegúrate de completar al menos:
1. ✅ FASE 0 + 1 + 2 → Sistema funcional básico
2. ✅ FASE 3 → Seguridad (es requisito obligatorio)
3. ✅ FASE 4 → Validaciones (es requisito obligatorio)
4. ⚠️ FASE 5 → Al menos tests unitarios básicos (Testcontainers es opcional)
5. ⚠️ FASE 6 → Al menos Docker Compose funcionando

### ⚡ Aceleradores
- **MapStruct**: Si no lo conoces, usa mappers manuales simples
- **Testcontainers**: Usa H2 en memoria si Testcontainers te da problemas
- **Swagger**: Opcional pero muy recomendado, agrega valor con poco esfuerzo
- **Métricas custom**: Empieza con las básicas, agrega más si hay tiempo

### 🚫 Errores comunes a evitar
- No sobrecomplicar la arquitectura hexagonal
- No agregar abstracciones innecesarias
- No perder tiempo en logging/métricas muy detalladas al inicio
- No intentar hacer todo perfecto desde el principio
- No olvidar probar cada fase antes de continuar

### ✅ Validación continua
Después de cada fase:
1. `mvn clean compile` → debe compilar sin errores
2. `mvn clean test` → tests deben pasar
3. Probar manualmente con Postman/cURL
4. Revisar logs de consola
5. Verificar base de datos

---

## Tiempo Total Estimado: 10-14 horas

| Fase   | Duración | Crítica   |
| ------ | -------- | --------- |
| FASE 0 | 1-2h     | ✅ Sí      |
| FASE 1 | 3-4h     | ✅ Sí      |
| FASE 2 | 2-3h     | ✅ Sí      |
| FASE 3 | 2-3h     | ✅ Sí      |
| FASE 4 | 1-2h     | ✅ Sí      |
| FASE 5 | 2-3h     | ⚠️ Parcial |
| FASE 6 | 1-2h     | ⚠️ Parcial |

**Total**: 12-19 horas (realista: 14-16 horas con pausas y debugging)

---

## ¿Listo para empezar?

Sugiero comenzar por **FASE 0** para tener una victoria rápida y validar el setup.

¿Empezamos? 🚀
