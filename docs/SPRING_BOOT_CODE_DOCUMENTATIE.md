# BA Totalisatie Spring Boot Applicatie - Uitgebreide Code Documentatie

## Overzicht
Deze documentatie beschrijft de volledige Spring Boot applicatie voor BA (Business Analytics) totalisatie op detailniveau. Elke map en elk bestand wordt uitgelegd met alle attributen, methoden, annotaties en configuraties.

---

## 📁 Root Directory

### `pom.xml` - Maven Project Configuratie

**Doel**: Maven project configuratie met alle dependencies en build instellingen.

**Project Metadata**:
- `groupId`: `com.totals` - Unieke identifier voor het project
- `artifactId`: `ba-totals` - Naam van de artifact
- `version`: `0.0.1-SNAPSHOT` - Development versie
- `name`: `ba-totals` - Display naam
- `description`: `BA Totalisatie Spring Boot Application` - Project beschrijving

**Java Configuratie**:
- `java.version`: `25` - Java versie (moderne versie)

**Parent POM**:
- `spring-boot-starter-parent`: `3.5.6` - Spring Boot parent POM voor dependency management

**Dependencies**:

1. **Spring Boot Web Starter** (`spring-boot-starter-web`)
   - REST API functionaliteit
   - Embedded Tomcat server
   - Spring MVC framework

2. **Spring Boot Data JPA** (`spring-boot-starter-data-jpa`)
   - JPA/Hibernate ORM
   - Database abstractie
   - Repository pattern ondersteuning

3. **SpringDoc OpenAPI** (`springdoc-openapi-starter-webmvc-ui:2.8.13`)
   - Swagger/OpenAPI documentatie
   - API endpoint documentatie
   - Interactive API testing

4. **PostgreSQL Driver** (`postgresql`)
   - Database driver voor PostgreSQL
   - Runtime scope (niet nodig tijdens compile)

5. **Spring Boot Test** (`spring-boot-starter-test`)
   - Testing framework
   - Test scope (alleen tijdens tests)

6. **Spring Boot Validation** (`spring-boot-starter-validation`)
   - Bean validation (JSR-303)
   - Input validatie annotaties

7. **Logback Classic** (`logback-classic`)
   - Logging framework
   - Geavanceerde logging configuratie

8. **Logstash Logback Encoder** (`logstash-logback-encoder:7.4`)
   - JSON logging output
   - Structured logging voor monitoring

**Build Plugins**:

1. **Spring Boot Maven Plugin**
   - Executable JAR creation
   - Lombok exclusion (niet gebruikt)

2. **Maven Compiler Plugin** (`3.13.0`)
   - Java 25 compilation
   - `source`: 25, `target`: 25, `release`: 25
   - Deprecation en warnings enabled

---

## 📁 `src/main/java/com/totals/` - Hoofdpakket

### `TotalsApplication.java` - Spring Boot Main Class

**Doel**: Entry point van de Spring Boot applicatie.

**Annotaties**:
- `@SpringBootApplication`: Combinatie van:
  - `@Configuration`: Spring configuratie klasse
  - `@EnableAutoConfiguration`: Automatische configuratie
  - `@ComponentScan`: Component scanning voor dependency injection

**Attributen**: Geen

**Methoden**:
- `main(String[] args)`: Static entry point
  - Start de Spring Boot applicatie
  - `SpringApplication.run(TotalsApplication.class, args)`

**Dependencies**: Geen (standalone main class)

---

## 📁 `src/main/java/com/totals/config/` - Configuratieklassen

**Voor volledige details zie [.CONFIG_DOCS.md](src/main/java/com/totals/config/.CONFIG_DOCS.md)**

De configuratie map bevat alle Spring Boot configuratieklassen voor validatie en CORS instellingen.

### `CorsConfig.java` - Cross-Origin Resource Sharing Configuratie

**Doel**: Configureert CORS voor cross-origin requests van frontend applicaties.

**Annotaties**:
- `@Configuration`: Spring configuratie klasse
- `implements WebMvcConfigurer`: Interface voor web configuratie

**Attributen**: Geen

**Methoden**:
- `addCorsMappings(CorsRegistry registry)`: Override van WebMvcConfigurer
  - Configureert CORS voor `/api/**` endpoints
  - `allowedOriginPatterns`: `http://localhost:*`, `http://127.0.0.1:*`
  - `allowedMethods`: GET, POST, PUT, DELETE, OPTIONS
  - `allowedHeaders`: Content-Type, Authorization, X-Requested-With, Accept, Origin
  - `allowCredentials`: true (cookies/authentication)
  - `maxAge`: 3600 seconden (preflight cache)

**Dependencies**: Geen


---

## 📁 `src/main/java/com/totals/controller/` - REST Controllers

**Voor volledige details zie [.CONTROLLER_DOCS.md](src/main/java/com/totals/controller/.CONTROLLER_DOCS.md)**

De controller map bevat alle REST API endpoints voor de BA Totalisatie applicatie.

### `BaController.java` - BA Totalisatie Controller

**Doel**: REST endpoints voor BA totalisatie berekeningen.

**Annotaties**:
- `@RestController`: REST controller met JSON response
- `@RequestMapping("/api/${api.version}")`: Base path met property substitution

**Attributen**:
- `logger`: `Logger` - SLF4J logger instance
- `baService`: `BaService` - Injected via constructor

**Constructor**:
- `BaController(BaService baService)`: Constructor injection

**Methoden**:
- `calculateTotals(String kenmerk, Integer bijdragePercentage)`: `@PostMapping("/ba/{kenmerk}/calculate-totals")`
  - **Parameters**:
    - `kenmerk`: `@PathVariable String` - Kenmerk voor groepering
    - `bijdragePercentage`: `@RequestParam Integer` - Bijdrage percentage (1-100)
  - **Return**: `ResponseEntity<BaCalculationResponse>`
  - **Functionaliteit**:
    - Start timing (`System.currentTimeMillis()`)
    - Haalt request timestamp op (`LocalDateTime.now()`)
    - Roept `baService.calculateTotals()` aan
    - Converteert resultaat naar response DTO
    - Berekent duration en returnt success response

**Dependencies**:
- `BaService` (injected)
- `BaCalculationResponse` (DTO)
- `BaTotalRecord` (model)

### `HealthController.java` - Health Check Controller

**Doel**: Health check endpoint voor monitoring.

**Annotaties**:
- `@RestController`: REST controller
- `@RequestMapping("/api/${api.version}/health")`: Health endpoint path

**Attributen**: Geen

**Methoden**:
- `healthCheck()`: `@GetMapping`
  - **Return**: `ResponseEntity<Map<String, Object>>`
  - **Functionaliteit**:
    - Returnt status "UP"
    - Service naam "BA Totals API"
    - Huidige timestamp

**Dependencies**: Geen

### `LogsController.java` - Logs Retrieval Controller

**Doel**: REST endpoint voor het ophalen van calculation logs.

**Annotaties**:
- `@RestController`: REST controller
- `@RequestMapping("/api/${api.version}/ba/{kenmerk}/logs")`: Logs endpoint met kenmerk path
- `@Validated`: Bean validation op class level

**Attributen**:
- `logger`: `Logger` - SLF4J logger instance
- `logService`: `LogService` - Injected via constructor

**Constructor**:
- `LogsController(LogService logService)`: Constructor injection

**Methoden**:
- `getLogsForKenmerk(String kenmerk, int last)`: `@GetMapping`
  - **Parameters**:
    - `kenmerk`: `@PathVariable @NotBlank @Pattern(regexp = "^[a-zA-Z0-9_-]+$") String` - Kenmerk voor log filtering
    - `last`: `@RequestParam @Min(1) @Max(1000) int` - Aantal laatste entries (default: `${api.validation.logs-default-entries:50}`)
  - **Return**: `ResponseEntity<BaLogsResponse>`
  - **Throws**: `java.io.IOException`
  - **Functionaliteit**:
    - Valideert input parameters via Bean validation
    - Roept `logService.getLogsForKenmerk()` aan
    - Returnt success response met log entries

**Dependencies**:
- `LogService` (injected)
- `BaLogsResponse` (DTO)

---

## 📁 `src/main/java/com/totals/dto/` - Data Transfer Objects

**Voor volledige details zie [.DTO_DOCS.md](src/main/java/com/totals/dto/.DTO_DOCS.md)**

De DTO map bevat alle Data Transfer Objects voor API communicatie.

### `BaCalculationResponse.java` - Calculation Response DTO

**Doel**: Response DTO voor BA totalisatie berekeningen.

**Type**: `record` (immutable data class)

**Attributen**:
- `message`: `String` - Success message
- `kenmerk`: `String` - Kenmerk waarvoor berekend
- `bijdragePercentage`: `Integer` - Gebruikt percentage
- `totalen`: `List<Double>` - Berekende totalen (5 waarden)
- `durationMs`: `long` - Berekening duration in milliseconden

**Methoden**:
- `success(String kenmerk, Integer bijdragePercentage, List<Double> totalen, long durationMs)`: Static factory method
  - Creëert success response met geformatteerde message

**Dependencies**: Geen

### `BaErrorResponse.java` - Error Response DTO

**Doel**: Standardized error response DTO.

**Type**: `record` (immutable data class)

**Attributen**:
- `timestamp`: `LocalDateTime` - Error timestamp
- `status`: `int` - HTTP status code
- `error`: `String` - Error type
- `message`: `String` - Error message
- `path`: `String` - Request path waar error optrad

**Methoden**:
- `of(int status, String error, String message, String path)`: Static factory method
  - Creëert error response met huidige timestamp

**Dependencies**: Geen

### `BaLogEntry.java` - Log Entry DTO

**Doel**: DTO voor individuele log entries.

**Type**: `record` (immutable data class)

**Attributen**:
- `timestamp`: `String` - Log timestamp
- `level`: `String` - Log level (INFO, ERROR, etc.)
- `message`: `String` - Log message
- `kenmerk`: `String` - Kenmerk uit log entry

**Dependencies**: Geen

### `BaLogsResponse.java` - Logs Response DTO

**Doel**: Response DTO voor log retrieval.

**Type**: `record` (immutable data class)

**Attributen**:
- `kenmerk`: `String` - Kenmerk waarvoor logs opgehaald
- `logEntries`: `List<BaLogEntry>` - Lijst van log entries

**Methoden**:
- `success(String kenmerk, List<BaLogEntry> logEntries)`: Static factory method
  - Creëert success response

**Dependencies**: `BaLogEntry`

### `BaTotalsResponse.java` - Totals Response DTO

**Doel**: DTO voor database aggregatie resultaten.

**Type**: `record` (immutable data class)

**Attributen**:
- `kenmerk`: `String` - Kenmerk
- `totaal1` tot `totaal6`: `Double` - Berekende totalen (6 waarden)
- `recordCount`: `Long` - Aantal records gebruikt in berekening

**Dependencies**: Geen

---

## 📁 `src/main/java/com/totals/exception/` - Exception Handling

**Voor volledige details zie [.EXCEPTION_DOCS.md](src/main/java/com/totals/exception/.EXCEPTION_DOCS.md)**

De exception map bevat alle exception handling klassen voor centrale foutafhandeling.

### `GlobalExceptionHandler.java` - Global Exception Handler

**Doel**: Centrale exception handling voor de hele applicatie.

**Annotaties**:
- `@ControllerAdvice`: Global exception handler voor alle controllers

**Attributen**:
- `logger`: `Logger` - SLF4J logger instance

**Methoden**:

1. `extractPath(WebRequest request)`: Private helper
   - Extraheert request path uit WebRequest
   - Verwijdert "uri=" prefix

2. `handleResponseStatusException(ResponseStatusException ex, WebRequest request)`: `@ExceptionHandler`
   - **Parameters**: ResponseStatusException, WebRequest
   - **Return**: `ResponseEntity<BaErrorResponse>`
   - **Functionaliteit**: Handelt ResponseStatusException af (404, 422, etc.)

3. `handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request)`: `@ExceptionHandler`
   - **Parameters**: IllegalArgumentException, WebRequest
   - **Return**: `ResponseEntity<BaErrorResponse>`
   - **Functionaliteit**: Handelt validatie errors af (400 Bad Request)

4. `handleDataAccessException(DataAccessException ex, WebRequest request)`: `@ExceptionHandler`
   - **Parameters**: DataAccessException, WebRequest
   - **Return**: `ResponseEntity<BaErrorResponse>`
   - **Functionaliteit**: Handelt database errors af (503 Service Unavailable)

5. `handleIOException(IOException ex, WebRequest request)`: `@ExceptionHandler`
   - **Parameters**: IOException, WebRequest
   - **Return**: `ResponseEntity<BaErrorResponse>`
   - **Functionaliteit**: Handelt file I/O errors af (500 Internal Server Error)

6. `handleGenericException(Exception ex, WebRequest request)`: `@ExceptionHandler`
   - **Parameters**: Exception, WebRequest
   - **Return**: `ResponseEntity<BaErrorResponse>`
   - **Functionaliteit**: Catch-all voor onverwachte errors (500 Internal Server Error)

**Dependencies**:
- `BaErrorResponse` (DTO)
- SLF4J Logger
- Spring Web classes

---

## 📁 `src/main/java/com/totals/model/` - JPA Entity Classes

**Voor volledige details zie [.MODEL_DOCS.md](src/main/java/com/totals/model/.MODEL_DOCS.md)**

De model map bevat alle JPA entity klassen voor database mapping.

### `BaRecord.java` - BA Record Entity

**Doel**: JPA entity voor BA records in de database.

**Annotaties**:
- `@Entity`: JPA entity
- `@Table(name = "ba_records")`: Database tabel naam
- `@Id`: Primary key
- `@GeneratedValue(strategy = GenerationType.IDENTITY)`: Auto-increment ID
- `@Column(name = "kenmerk")`: Database kolom mapping
- `@NotBlank`, `@NotNull`: Bean validation constraints

**Attributen**:
- `id`: `Long` - Primary key (auto-generated)
- `kenmerk`: `String` - Kenmerk voor groepering (validated: not blank, not null)
- `cijfer1` tot `cijfer6`: `Double` - Numerieke waarden (validated: not null)

**Methoden**:
- Getter/setter pairs voor alle attributen
- Standard JavaBean pattern

**Dependencies**:
- Jakarta Persistence API
- Jakarta Validation API

### `BaTotalRecord.java` - BA Total Record Entity

**Doel**: JPA entity voor berekende BA totalen.

**Annotaties**:
- `@Entity`: JPA entity
- `@Table(name = "ba_totals")`: Database tabel naam
- `@Id`: Primary key
- `@GeneratedValue(strategy = GenerationType.IDENTITY)`: Auto-increment ID
- `@Column(name = "kenmerk")`: Database kolom mapping
- `@NotBlank`, `@NotNull`: Bean validation constraints
- `@JsonIgnore`: Exclude from JSON serialization

**Attributen**:
- `id`: `Long` - Primary key (auto-generated)
- `kenmerk`: `String` - Kenmerk (validated: not blank, not null)
- `totaal1` tot `totaal5`: `Double` - Berekende totalen (validated: not null)
- `requestTimestamp`: `Long` - Request timestamp (JSON ignored)

**Methoden**:
- Getter/setter pairs voor alle attributen
- Standard JavaBean pattern

**Dependencies**:
- Jakarta Persistence API
- Jakarta Validation API
- Jackson JSON annotations

---

## 📁 `src/main/java/com/totals/repository/` - Data Access Layer

**Voor volledige details zie [.REPOSITORY_DOCS.md](src/main/java/com/totals/repository/.REPOSITORY_DOCS.md)**

De repository map bevat alle Spring Data JPA repository interfaces voor data access.

### `BaRecordRepository.java` - BA Record Repository

**Doel**: Data access interface voor BaRecord entities.

**Annotaties**:
- `@Repository`: Spring repository component
- `@Transactional(readOnly = true)`: Read-only transactions
- `@Query`: Custom JPQL/native queries
- `@Param`: Named parameters

**Interface**: `extends JpaRepository<BaRecord, Long>`

**Methoden**:

1. `findByKenmerk(String kenmerk)`: `@Transactional(readOnly = true)`
   - **Parameters**: `kenmerk` - Kenmerk voor filtering
   - **Return**: `List<BaRecord>`
   - **Functionaliteit**: Vindt alle records met specifiek kenmerk

2. `countByKenmerk(String kenmerk)`: `@Transactional(readOnly = true)`
   - **Parameters**: `kenmerk` - Kenmerk voor counting
   - **Return**: `long`
   - **Functionaliteit**: Telt records met specifiek kenmerk

3. `calculateTotalsByKenmerk(String kenmerk)`: `@Query` (native SQL)
   - **Parameters**: `@Param("kenmerk") String kenmerk`
   - **Return**: `Optional<BaTotalsResponse>`
   - **Functionaliteit**: 
     - Native SQL query voor database-level aggregatie
     - SUM van alle cijfer kolommen per kenmerk
     - COUNT van records
     - Returnt BaTotalsResponse DTO

**Dependencies**:
- `BaRecord` (entity)
- `BaTotalsResponse` (DTO)
- Spring Data JPA

### `BaTotalRepository.java` - BA Total Repository

**Doel**: Data access interface voor BaTotalRecord entities.

**Annotaties**:
- `@Repository`: Spring repository component
- `@Modifying`: Modifying query (UPDATE/DELETE)
- `@Query`: Custom JPQL query
- `@Param`: Named parameters

**Interface**: `extends JpaRepository<BaTotalRecord, Long>`

**Methoden**:

1. `findByKenmerk(String kenmerk)`
   - **Parameters**: `kenmerk` - Kenmerk voor lookup
   - **Return**: `Optional<BaTotalRecord>`
   - **Functionaliteit**: Vindt totaal record per kenmerk

2. `updateIfNewer(...)`: `@Modifying` + `@Query`
   - **Parameters**: 
     - `kenmerk`: `String` - Kenmerk
     - `totaal1` tot `totaal5`: `Double` - Nieuwe totaal waarden
     - `newTimestamp`: `Long` - Nieuwe timestamp
   - **Return**: `int` - Aantal geüpdatete rijen
   - **Functionaliteit**:
     - JPQL UPDATE query
     - Update alleen als nieuwe timestamp ouder is
     - Optimistic locking via timestamp vergelijking
     - Returnt aantal affected rows

**Dependencies**:
- `BaTotalRecord` (entity)
- Spring Data JPA

---

## 📁 `src/main/java/com/totals/service/ba/` - BA Business Logic

**Voor volledige details zie [.SERVICE_BA_DOCS.md](src/main/java/com/totals/service/ba/.SERVICE_BA_DOCS.md)**

De service/ba map bevat alle business logic services voor BA berekeningen.

### `BaCalculator.java` - BA Calculation Logic

**Doel**: Pure business logic voor BA berekeningen.

**Annotaties**:
- `@Component`: Spring component

**Attributen**: Geen

**Methoden**:

1. `performCalculations(BaTotalsResponse agg, Integer percentage)`
   - **Parameters**:
     - `agg`: `BaTotalsResponse` - Geaggregeerde data uit database
     - `percentage`: `Integer` - Bijdrage percentage
   - **Return**: `BaTotalRecord` - Berekende totalen
   - **Functionaliteit**:
     - Converteert percentage naar decimal (percentage / 100.0)
     - Berekent cijfer7 = agg.totaal1()
     - Berekent cijfer1 via `calculateCijfer1()`
     - Berekent totaal1 = cijfer1
     - Berekent totaal2 = (agg.totaal3() / cijfer7) * cijfer1
     - Berekent totaal3 = (agg.totaal4() / cijfer7) * cijfer1
     - Berekent totaal4 = agg.totaal5() * bijdrage
     - Berekent totaal5 = agg.totaal6()
     - Creëert en vult BaTotalRecord

2. `calculateCijfer1(double totaal1, double totaal2, double bijdrage)`
   - **Parameters**: Totaal waarden en bijdrage
   - **Return**: `double` - Berekende cijfer1
   - **Functionaliteit**:
     - Berekent threshold = totaal2 / bijdrage
     - Returnt minimum van totaal1 en threshold

**Dependencies**:
- `BaTotalsResponse` (DTO)
- `BaTotalRecord` (entity)

### `BaService.java` - BA Service Orchestrator

**Doel**: Hoofdservice die BA berekeningen orchestreert.

**Annotaties**:
- `@Service`: Spring service component
- `@Transactional`: Transactional method

**Attributen**:
- `logger`: `Logger` - SLF4J logger
- `recordRepository`: `BaRecordRepository` - Injected
- `totalRepository`: `BaTotalRepository` - Injected
- `baValidator`: `BaValidator` - Injected
- `baCalculator`: `BaCalculator` - Injected

**Constructor**:
- `BaService(...)`: Constructor injection van alle dependencies

**Methoden**:

1. `calculateTotals(String kenmerk, Integer bijdragePercentage, LocalDateTime requestTime)`
   - **Annotaties**: `@Transactional`
   - **Parameters**: Kenmerk, percentage, request timestamp
   - **Return**: `BaTotalRecord` - Berekend totaal
   - **Functionaliteit**:
     - Valideert inputs via `baValidator`
     - Haalt geaggregeerde data op via `recordRepository.calculateTotalsByKenmerk()`
     - Valideert divisor (geen division by zero)
     - Voert berekeningen uit via `baCalculator`
     - Slaat/update totaal op via `saveOrUpdateTotal()`


2. `saveOrUpdateTotal(String kenmerk, BaTotalRecord calculated, LocalDateTime requestTime)`: `@Transactional`
   - **Parameters**: Kenmerk, berekende totalen, request timestamp
   - **Return**: `BaTotalRecord` - Opgeslagen/geüpdatete record
   - **Functionaliteit**:
     - Converteert timestamp naar epoch milliseconds
     - Probeert update via `totalRepository.updateIfNewer()`
     - Als update succesvol: haalt geüpdatete record op
     - Als geen update: controleert of record bestaat
     - Als bestaat: logt warning en returnt bestaande
     - Als niet bestaat: creëert nieuw record

3. `createNewTotal(String kenmerk, BaTotalRecord source)`: Private
   - **Parameters**: Kenmerk en source record
   - **Return**: `BaTotalRecord` - Nieuw record
   - **Functionaliteit**: Kopieert waarden naar nieuw BaTotalRecord

**Dependencies**:
- `BaRecordRepository`, `BaTotalRepository` (repositories)
- `BaValidator`, `BaCalculator` (services)
- SLF4J Logger
- Spring Retry, Transaction, Web

### `BaValidator.java` - Input Validation Service

**Doel**: Centraliseert input validatie voor BA service.

**Annotaties**:
- `@Component`: Spring component

**Attributen**:
- `logger`: `Logger` - SLF4J logger instance

**Constructor**: Geen (default constructor)

**Methoden**:

1. `validateInputs(String kenmerk, Integer bijdragePercentage)`
   - **Parameters**: Kenmerk en percentage
   - **Functionaliteit**:
     - Valideert kenmerk: null/empty check, lengte (1-50), regex pattern
     - Valideert bijdragePercentage: null check, range (1-100)
     - Gooit IllegalArgumentException bij fouten

2. `validateDivisor(double value, String kenmerk)`
   - **Parameters**: Waarde en kenmerk (voor error message)
   - **Functionaliteit**:
     - Controleert op division by zero
     - Gooit ResponseStatusException (422) als waarde 0

**Dependencies**:
- Spring Web (ResponseStatusException)
- SLF4J Logger

---

## 📁 `src/main/java/com/totals/service/logging/` - Logging Service

**Voor volledige details zie [.SERVICE_LOGGING_DOCS.md](src/main/java/com/totals/service/logging/.SERVICE_LOGGING_DOCS.md)**

De service/logging map bevat alle logging services voor log file processing.

### `LogService.java` - Log File Processing Service

**Doel**: Service voor het lezen en parsen van log bestanden.

**Annotaties**:
- `@Service`: Spring service component

**Attributen**:
- `KENMERK_PATTERN`: `Pattern` - Regex voor kenmerk extractie
- `logPath`: `String` - Log directory path (injected via `@Value`)
- `calculationLogFile`: `String` - Calculation log filename (injected via `@Value`)
- `objectMapper`: `ObjectMapper` - Jackson JSON parser

**Constructor**:
- `LogService(@Value("${logging.path:./logs}") String logPath, @Value("${logging.calculation.filename:ba-calc.log}") String calculationLogFile)`
  - Constructor injection met default values

**Methoden**:

1. `getLogsForKenmerk(String kenmerk, int last)`
   - **Parameters**: Kenmerk en aantal entries
   - **Return**: `List<BaLogEntry>` - Gefilterde log entries
   - **Functionaliteit**:
     - Valideert input parameters
     - Controleert of log file bestaat
     - Parseert log file en filtert op kenmerk
     - Returnt laatste N entries

2. `parseLogFile(Path logFilePath, String kenmerk, int last)`: Private
   - **Parameters**: Log file path, kenmerk, aantal entries
   - **Return**: `List<BaLogEntry>` - Parseerde entries
   - **Functionaliteit**:
     - Controleert file readability
     - Leest alle regels uit file
     - Filtert regels op kenmerk
     - Neemt laatste N entries
     - Parseert regels naar BaLogEntry objects

3. `parseLogLines(List<String> logLines)`: Private
   - **Parameters**: `logLines` - Te parsen log regels
   - **Return**: `List<BaLogEntry>` - Parseerde entries
   - **Functionaliteit**:
     - Parseert elke regel als JSON
     - Extraheert timestamp, level, message, kenmerk
     - Creëert BaLogEntry objects
     - Skip corrupte regels

4. `getStringValue(JsonNode jsonNode, String fieldName)`: Private
   - **Parameters**: JSON node en field naam
   - **Return**: `String` - Field waarde of lege string
   - **Functionaliteit**: Veilige string extractie uit JSON

5. `containsKenmerk(String line, String kenmerk)`: Private
   - **Parameters**: Log regel en kenmerk
   - **Return**: `boolean` - Of regel kenmerk bevat
   - **Functionaliteit**: Regex matching voor kenmerk extractie

6. `validateInput(String kenmerk, int last)`: Private
   - **Parameters**: Kenmerk en aantal entries
   - **Functionaliteit**:
     - Valideert kenmerk (niet null/empty)
     - Valideert last (1-1000 range)
     - Gooit IllegalArgumentException bij fouten

**Dependencies**:
- Jackson ObjectMapper (JSON parsing)
- Spring `@Value` injection
- Java NIO (Files, Paths)

---

## 📁 `src/main/resources/` - Configuratiebestanden

**Voor volledige details zie [.RESOURCES_DOCS.md](src/main/resources/.RESOURCES_DOCS.md)**

De resources map bevat alle configuratiebestanden voor de Spring Boot applicatie.

### `application.properties` - Spring Boot Configuratie

**Doel**: Centrale configuratie voor de Spring Boot applicatie.

**Secties**:

1. **Database Configuratie**:
   - `spring.datasource.url`: Database URL met environment variable support
   - `spring.datasource.username`: Database username
   - `spring.datasource.password`: Database password

2. **Connection Pool Configuratie**:
   - `spring.datasource.hikari.maximum-pool-size`: 1 (single-threaded)
   - `spring.datasource.hikari.minimum-idle`: 1

3. **Tomcat Thread Configuratie**:
   - `server.tomcat.threads.max`: 1 (single-threaded)
   - `server.tomcat.threads.min-spare`: 1

4. **JPA/Hibernate Configuratie**:
   - `spring.jpa.hibernate.ddl-auto`: validate (geen schema wijzigingen)
   - `spring.jpa.open-in-view`: false (performance optimalisatie)

5. **Logging Configuratie**:
   - `logging.path`: Log directory (environment variable support)
   - `logging.level.root`: Log level (environment variable support)
   - `logging.file.application.max-size`: 10MB
   - `logging.file.application.max-history`: 30 dagen

6. **API Configuratie**:
   - `api.version`: v1

7. **Calculation Log Configuratie**:
   - `logging.calculation.filename`: ba-calc.log

8. **OpenAPI/Swagger Configuratie**:
   - `springdoc.api-docs.path`: /v3/api-docs
   - `springdoc.swagger-ui.path`: /swagger-ui.html
   - `springdoc.swagger-ui.enabled`: true
   - `springdoc.swagger-ui.operationsSorter`: method
   - `springdoc.swagger-ui.tagsSorter`: alpha
   - `springdoc.swagger-ui.tryItOutEnabled`: true

**Environment Variables Support**:
- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
- `LOG_PATH`, `LOG_LEVEL`

### `logback-spring.xml` - Logback Logging Configuratie

**Doel**: Geavanceerde logging configuratie met Logback.

**Configuratie Properties**:
- `LOG_PATH`: Log directory (via Spring property)
- `LOG_LEVEL`: Root log level (via Spring property)
- `LOG_APP_MAX_SIZE`: Max file size (via Spring property)
- `LOG_APP_MAX_HISTORY`: Max history (via Spring property)

**Appenders**:

1. **APPLICATION_FILE Appender**:
   - **Type**: `RollingFileAppender`
   - **File**: `${LOG_PATH}/app.log`
   - **Rolling Policy**: `SizeAndTimeBasedRollingPolicy`
   - **Pattern**: `${LOG_PATH}/app.%d{yyyy-MM-dd}.%i.log`
   - **Max Size**: `${LOG_APP_MAX_SIZE}`
   - **Max History**: `${LOG_APP_MAX_HISTORY}`
   - **Encoder**: `LogstashEncoder` (JSON format)

2. **CALCULATION_FILE Appender**:
   - **Type**: `RollingFileAppender`
   - **File**: `${LOG_PATH}/ba-calc.log`
   - **Rolling Policy**: `SizeAndTimeBasedRollingPolicy`
   - **Pattern**: `${LOG_PATH}/ba-calc.%d{yyyy-MM-dd}.%i.log`
   - **Max Size**: `${LOG_APP_MAX_SIZE}`
   - **Max History**: `${LOG_APP_MAX_HISTORY}`
   - **Encoder**: `LogstashEncoder` (JSON format)

**Loggers**:

1. **BA Services Logger**:
   - **Name**: `com.totals.service.ba`
   - **Level**: INFO
   - **Additivity**: false
   - **Appender**: CALCULATION_FILE (alleen naar calculation log)

2. **Exception Handler Logger**:
   - **Name**: `com.totals.exception.GlobalExceptionHandler`
   - **Level**: INFO
   - **Additivity**: false
   - **Appenders**: CALCULATION_FILE + APPLICATION_FILE

3. **Root Logger**:
   - **Level**: `${LOG_LEVEL}`
   - **Appender**: APPLICATION_FILE

**Features**:
- JSON logging output (LogstashEncoder)
- Log rotation op basis van size en time
- Separate logs voor BA calculations
- Configureerbare paths en levels via Spring properties

---

## 🔄 Component Interactie Diagram

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   BaController  │───▶│    BaService    │───▶│  BaCalculator   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │                       ▼                       │
         │              ┌─────────────────┐              │
         │              │  BaValidator    │              │
         │              └─────────────────┘              │
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│ BaRecordRepo    │    │ BaTotalRepo     │    │   LogService    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   BaRecord      │    │ BaTotalRecord   │    │  Log Files      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 🎯 Samenvatting

Deze Spring Boot applicatie implementeert een complete BA totalisatie systeem met:

- **Single-threaded architectuur** voor voorspelbare resultaten
- **RESTful API** met Swagger documentatie
- **PostgreSQL database** met JPA/Hibernate
- **Uitgebreide validatie** en error handling
- **Structured logging** met JSON output
- **Environment variable support** voor flexibele deployment
- **Health monitoring** via custom health endpoint

Elk component is zorgvuldig ontworpen met duidelijke verantwoordelijkheden en minimale coupling, wat resulteert in een maintainable en schaalbare applicatie.

---

## 📚 Documentatie Overzicht

Deze applicatie heeft uitgebreide documentatie per map met gedetailleerde technische beschrijvingen:

### Detail Documentatie per Map

| Map | Documentatie Bestand | Beschrijving |
|-----|---------------------|--------------|
| **Config** | [.CONFIG_DOCS.md](src/main/java/com/totals/config/.CONFIG_DOCS.md) | Configuratieklassen voor validatie en CORS |
| **Controller** | [.CONTROLLER_DOCS.md](src/main/java/com/totals/controller/.CONTROLLER_DOCS.md) | REST API endpoints en controllers |
| **DTO** | [.DTO_DOCS.md](src/main/java/com/totals/dto/.DTO_DOCS.md) | Data Transfer Objects voor API communicatie |
| **Exception** | [.EXCEPTION_DOCS.md](src/main/java/com/totals/exception/.EXCEPTION_DOCS.md) | Exception handling en error responses |
| **Model** | [.MODEL_DOCS.md](src/main/java/com/totals/model/.MODEL_DOCS.md) | JPA entity klassen voor database mapping |
| **Repository** | [.REPOSITORY_DOCS.md](src/main/java/com/totals/repository/.REPOSITORY_DOCS.md) | Spring Data JPA repository interfaces |
| **Service/BA** | [.SERVICE_BA_DOCS.md](src/main/java/com/totals/service/ba/.SERVICE_BA_DOCS.md) | Business logic services voor BA berekeningen |
| **Service/Logging** | [.SERVICE_LOGGING_DOCS.md](src/main/java/com/totals/service/logging/.SERVICE_LOGGING_DOCS.md) | Logging services voor file processing |
| **Resources** | [.RESOURCES_DOCS.md](src/main/resources/.RESOURCES_DOCS.md) | Configuratiebestanden en properties |

### Documentatie Kenmerken

- **Consistente Structuur**: Alle detail documentatie volgt dezelfde structuur
- **Uitgebreide Details**: Annotaties, attributen, methoden, dependencies per bestand
- **Code Voorbeelden**: Volledige method signatures en code snippets
- **Business Logic**: Berekening formules en business rules gedocumenteerd
- **Troubleshooting**: Veelvoorkomende problemen en debug tips
- **Best Practices**: Geïmplementeerde patterns en practices

### Gebruik van Documentatie

1. **Ontwikkeling**: Gebruik detail docs voor begrip van componenten
2. **Onderhoud**: Referentie voor wijzigingen en uitbreidingen
3. **Troubleshooting**: Debug tips en veelvoorkomende problemen
4. **AI Tools**: Prompt voor code generatie en analyse
