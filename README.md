# BA Totalisatie Systeem

Een Spring Boot applicatie voor het berekenen en beheren van BA

## 📋 Overzicht

Dit systeem biedt een complete backend oplossing voor het berekenen en beheren van BA totalen met de volgende componenten:
- **Backend**: Spring Boot REST API met PostgreSQL database
- **Database**: PostgreSQL met geoptimaliseerde queries
- **API Documentatie**: Swagger/OpenAPI integratie
- **Single-threaded**: Geoptimaliseerd voor sequentiële verwerking

## 🏗️ Architectuur

### Threading Configuratie

**Standaard: Single-Threaded**
- 1 Tomcat thread, 1 database connectie
- Sequentiële verwerking, voorspelbaar gedrag
- Gegarandeerd thread-safe

**Optioneel: Multi-Threaded**
Om multi-threading in te schakelen, wijzig in `application.properties`:
```properties
server.tomcat.threads.max=20
spring.datasource.hikari.maximum-pool-size=10
```
- 20 Tomcat threads, 10 database connecties  
- Parallelle verwerking, hogere throughput
- Thread-safe via @Transactional en optimistic locking

```
├── src/main/java/com/totals/     # Spring Boot backend
│   ├── controller/               # REST controllers
│   ├── service/                  # Business logic
│   ├── repository/               # Data access layer
│   ├── model/                    # Entity classes
│   ├── dto/                      # Data Transfer Objects
│   ├── exception/                # Custom exceptions
│   └── config/                   # Configuration classes
├── database/                     # Database scripts
├── logs/                         # Application logs
└── target/                       # Maven build output
```

## 🚀 Technologieën

### Backend
- **Java 25** - Moderne Java versie
- **Spring Boot 3.5.6** - Framework voor microservices
- **Spring Data JPA** - Database ORM
- **PostgreSQL** - Relational database
- **SpringDoc OpenAPI 2.8.13** - API documentatie
- **Maven** - Dependency management
- **Logback** - Geavanceerde logging
- **Spring Retry** - Fault tolerance
- **Spring Validation** - Input validatie
- **Spring Actuator** - Health monitoring

## 📦 Vereisten

- Java 25 of hoger
- Maven 3.6+
- PostgreSQL 12+

## ⚙️ Installatie

### 1. Database Setup

```bash
# Ga naar de database directory
cd database
```

Voer setup.bat uit, door op bestand te klikken in je directory 

### 2. Backend Setup

```bash
# Installeer dependencies
mvn clean install

# Start de applicatie
mvn spring-boot:run
```

De backend API is beschikbaar op: `http://localhost:8080`

## 📚 API Documentatie

De API endpoints zijn beschikbaar via de backend op `http://localhost:8080`.

### Belangrijkste Endpoints

| Methode | Endpoint | Beschrijving |
|---------|----------|--------------|
| `POST` | `/api/v1/ba/{kenmerk}/calculate-totals?bijdragePercentage={percentage}` | Berekent totalen voor een kenmerk met bijdragepercentage |
| `GET` | `/api/v1/ba/{kenmerk}/logs?last={aantal}` | Haalt calculation logs op voor een kenmerk |
| `GET` | `/api/v1/health` | Health check endpoint |

### API Parameters

#### Calculate Totals Endpoint
- **kenmerk** (path parameter): Alphanumeric string (1-50 chars, pattern: `^[a-zA-Z0-9_-]+$`)
- **bijdragePercentage** (query parameter): Integer tussen 1-100 (representeert percentage)

#### Logs Endpoint
- **kenmerk** (path parameter): Alphanumeric string (1-50 chars, pattern: `^[a-zA-Z0-9_-]+$`)
- **last** (query parameter, optioneel): Aantal laatste log entries (default: 50, max: 1000)

## 🗄️ Database Schema

### BaRecord Tabel
- `id` - Primary key
- `kenmerk` - Kenmerk voor groepering
- `cijfer1` tot `cijfer5` - Numerieke waarden voor berekening

### BaTotal Tabel
- `id` - Primary key
- `kenmerk` - Kenmerk voor identificatie
- `totaal1` tot `totaal5` - Berekende totalen

## 🧮 Berekening Logica

### BA Totalisatie Proces
1. **Input**: Kenmerk + Bijdragepercentage (1-100%)
2. **Data Retrieval**: Alle BaRecord records met het opgegeven kenmerk
3. **Calculation**: Voor elke cijfer1-5 kolom:
   - Som van alle waarden voor dat kenmerk
   - Vermenigvuldigd met bijdragepercentage/100
4. **Storage**: Resultaten opgeslagen in BaTotal tabel
5. **Response**: Berekende totalen met performance metrics

### Voorbeeld
```
Kenmerk: "PROJECT_A"
Bijdragepercentage: 40%
Records: 
  - Record 1: cijfer1=100, cijfer2=200, cijfer3=300
  - Record 2: cijfer1=50,  cijfer2=150, cijfer3=250

Berekening:
- Totaal1 = (100 + 50) * 0.40 = 60
- Totaal2 = (200 + 150) * 0.40 = 140  
- Totaal3 = (300 + 250) * 0.40 = 220
```

## 🔧 Configuratie

### Backend (application.properties)
```properties
# Database configuratie (met environment variabelen ondersteuning)
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/ba_totals_db}
spring.datasource.username=${DB_USERNAME:ba_user}
spring.datasource.password=${DB_PASSWORD:ba_pass}

# Single-threaded configuratie
spring.datasource.hikari.maximum-pool-size=1
spring.datasource.hikari.minimum-idle=1
server.tomcat.threads.max=1
server.tomcat.threads.min-spare=1

# JPA configuratie
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.open-in-view=false

# Logging configuratie
logging.path=${LOG_PATH:./logs}
logging.level.root=${LOG_LEVEL:INFO}

# Retry configuratie
spring.retry.max-attempts=${RETRY_MAX_ATTEMPTS:3}
spring.retry.backoff.delay=${RETRY_BACKOFF_DELAY:1000}
spring.retry.backoff.multiplier=${RETRY_BACKOFF_MULTIPLIER:2.0}

# API configuratie
api.version=v1
api.validation.kenmerk-max-length=50
api.validation.kenmerk-min-length=1
api.validation.kenmerk-pattern=^[a-zA-Z0-9_-]+$
api.validation.bijdrage-percentage-min=1
api.validation.bijdrage-percentage-max=100
api.validation.logs-default-entries=50

# Actuator configuratie
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=always
```

### Environment Variabelen
Het systeem ondersteunt environment variabelen voor flexibele configuratie:
- `DB_URL` - Database URL
- `DB_USERNAME` - Database gebruikersnaam
- `DB_PASSWORD` - Database wachtwoord
- `LOG_PATH` - Log directory pad
- `LOG_LEVEL` - Logging niveau
- `RETRY_MAX_ATTEMPTS` - Maximum aantal retry pogingen
- `RETRY_BACKOFF_DELAY` - Retry delay in milliseconden
- `RETRY_BACKOFF_MULTIPLIER` - Retry backoff multiplier


## 🧪 Testing

```bash
# Backend tests uitvoeren
mvn test

# Specifieke test klasse
mvn test -Dtest=SimpleConcurrentTest
```

## 📊 Functionaliteiten

### Backend Features
- ✅ RESTful API met Swagger documentatie
- ✅ Efficiënte database aggregatie
- ✅ Single-threaded processing voor consistente resultaten
- ✅ Uitgebreide error handling
- ✅ Custom exceptions
- ✅ JPA repositories met custom queries
- ✅ Geavanceerde logging met Logback
- ✅ Environment variabelen ondersteuning
- ✅ Spring Retry voor fault tolerance
- ✅ Health check endpoints
- ✅ Input validatie met Spring Validation
- ✅ API versioning (v1)
- ✅ Bijdragepercentage berekening

## 🚨 Error Handling

Het systeem heeft uitgebreide error handling:
- `RecordNotFoundException` - Wanneer geen records gevonden worden
- `CalculationException` - Bij berekeningsfouten
- `GlobalExceptionHandler` - Centrale error handling
- `ErrorCode` - Gestandaardiseerde error codes

## 📈 Performance

- **Single-threaded Processing**: Alle requests worden sequentieel afgehandeld voor consistente resultaten
- **Database Optimalisatie**: Efficiënte queries met JPA
- **Minimale Connection Pool**: Single connection voor resource efficiency
- **Retry Mechanism**: Automatische retry bij database fouten
- **Schema Validatie**: DDL-auto=validate voor productie stabiliteit

## 🔒 Security

- Input validatie op alle endpoints met Spring Validation
- SQL injection bescherming via JPA
- Error message sanitization
- CORS configuratie voor cross-origin requests
- Kenmerk pattern validatie (alphanumeric + underscore/dash)
- Bijdragepercentage range validatie (1-100%)

## 📝 Logging

Het systeem gebruikt uitgebreide logging:
- Console logging voor debugging
- Error tracking met stack traces
- Performance monitoring
- Database query logging
- Configureerbare log niveaus
- Logback configuratie met JSON output
- Separate log files voor verschillende componenten
- Calculation-specific logging naar `ba-calculation-default.log`
- Configurable log path via environment variables
- Log rotation met max-size en max-history

## 🚀 Deployment & Gebruik

### Quick Start
```bash
# 1. Database setup
cd database
setup_database.bat

# 2. Start applicatie
mvn spring-boot:run

# 3. Test endpoints
curl -X POST "http://localhost:8080/api/v1/ba/TEST_PROJECT/calculate-totals?bijdragePercentage=50"
curl -X GET "http://localhost:8080/api/v1/ba/TEST_PROJECT/logs?last=10"
curl -X GET "http://localhost:8080/api/v1/health"
```

### Production Deployment
```bash
# Build JAR
mvn clean package

# Run JAR
java -jar target/ba-totals-0.0.1-SNAPSHOT.jar

# Met environment variabelen
java -jar target/ba-totals-0.0.1-SNAPSHOT.jar \
  --DB_URL=jdbc:postgresql://prod-db:5432/ba_totals_db \
  --DB_USERNAME=prod_user \
  --DB_PASSWORD=prod_pass \
  --LOG_LEVEL=WARN
```

### Monitoring
- **Health Check**: `GET /api/v1/health`
- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **Logs**: `./logs/` directory
- **Actuator**: `http://localhost:8080/actuator/health`


