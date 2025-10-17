# Code Review Prompt - BA Totals Spring Boot App

## Doel
Verwijder overbodige code en behoud kernfunctionaliteit voor een clean, simpele codebase zonder "AI hallucinaties".

## Kernfunctionaliteit (MOET BEHOUDEN)

### 1. Database Aggregatie
- **Repository queries** voor efficiënte totaal berekening via `BaRecordRepository.calculateTotalsByKenmerk()`
- **Native SQL aggregatie** met SUM operaties en COALESCE voor null handling
- **6 cijfers input** → database aggregatie → **5 totalen output**

### 2. Timestamp-based Optimistic Locking
- **Concurrency control** via `requestTimestamp` in `BaTotalRecord`
- **Conditional updates** met `updateIfNewer()` in repository
- **Single-threaded processing** (max 1 thread, 1 connection)

### 3. Retry Mechanisme
- **Exponential backoff** voor database fouten via `@Retryable`
- **DataAccessException** en `SQLException` retry logic
- **BusinessException** wordt NIET geretryd

### 4. Input Validatie
- **Spring Validation** met `@NotNull`, `@NotBlank` annotations
- **Custom validator** `BaValidator` voor business rules
- **Configuration properties** voor validatie regels

### 5. Custom Error Handling
- **Business exceptions** met error codes (BA-001, BA-004, etc.)
- **GlobalExceptionHandler** voor gecentraliseerde error handling
- **Structured error responses** met `BaErrorResponse`

### 6. Structured Logging
- **Specifieke log files** (`ba-calc.log`, `app.log`)
- **JSON structured logging** met logback
- **LogService** voor log retrieval

### 7. Berekening Logica (KERN - NIET AANRAKEN)
```
Cijfer 1: if cijfer1 > cijfer2/bijdragepercentage then cijfer2/bijdragepercentage else cijfer1
Cijfer 2: (cijfer3/cijfer7) * cijfer1  
Cijfer 3: (cijfer4/cijfer7) * cijfer1
Cijfer 4: cijfer5 * bijdragepercentage
Cijfer 5: cijfer6 (directe sum)
```

## Te Verwijderen

### 1. Overbodige Comments
- **JavaDoc comments** die alleen de code herhalen
- **Inline comments** die voor de hand liggen
- **TODO comments** en development notes
- **Boilerplate documentatie** in controllers

### 2. Ongebruikte Imports
- **Unused imports** in alle Java bestanden
- **Wildcard imports** vervangen door specifieke imports
- **Redundant imports** van dezelfde package

### 3. Ongebruikte Methodes
- **Helper methodes** die niet worden aangeroepen
- **Duplicate methodes** met dezelfde functionaliteit
- **Legacy methodes** die niet meer nodig zijn

### 4. Overmatige Logging
- **Debug logging** statements in productie code
- **Redundant logging** van dezelfde informatie
- **Verbose logging** zonder toegevoegde waarde

### 5. Dubbele Validaties
- **Redundant validation** op meerdere lagen
- **Duplicate validation logic** in verschillende classes
- **Over-engineering** van validatie

### 6. Overbodige Code
- **Empty constructors** zonder functionaliteit
- **Unused fields** en variabelen
- **Dead code** dat nooit wordt uitgevoerd

## Specifieke Aandachtspunten per Bestand

### Controllers
- **BaController**: Behoud core endpoint, verwijder overbodige Swagger annotations
- **HealthController**: Minimaliseer tot essentiële health check
- **LogsController**: Behoud log retrieval functionaliteit

### Services
- **BaService**: Behoud retry logic en timestamp handling
- **BaCalculator**: Behoud berekening logica, verwijder overbodige helper methodes
- **BaValidator**: Behoud input validatie, verwijder duplicate checks
- **LogService**: Behoud log parsing, verwijder overmatige error handling

### Models
- **BaRecord**: Behoud JPA annotations en getters/setters
- **BaTotalRecord**: Behoud timestamp en totaal fields

### Exception Handling
- **GlobalExceptionHandler**: Behoud error mapping, verwijder overmatige logging
- **BusinessException**: Behoud error codes, minimaliseer constructors

### Configuration
- **ValidationProperties**: Behoud configuratie, verwijder ongebruikte properties
- **CorsConfig**: Minimaliseer tot essentiële CORS settings

## Review Checklist

Voor elk bestand:
- [ ] Zijn alle imports nodig?
- [ ] Zijn alle methodes gebruikt?
- [ ] Zijn comments nuttig of redundant?
- [ ] Is logging gepast of overmatig?
- [ ] Zijn validaties niet dubbel?
- [ ] Is de code simpel en clean?
- [ ] Wordt kernfunctionaliteit beschermd?

## Output Verwachting

Na cleanup:
- **Minimale code** zonder overbodige elementen
- **Duidelijke structuur** met logische scheiding
- **Behouden functionaliteit** voor alle core features
- **Clean imports** en dependencies
- **Appropriate logging** zonder spam
- **Simple validation** zonder over-engineering

## Waarschuwing

**NIET VERWIJDEREN:**
- Database aggregatie queries
- Retry mechanisme annotations
- Timestamp-based locking logic
- Berekening algoritmes in BaCalculator
- Error handling structure
- Core business logic

**WEL VERWIJDEREN:**
- Redundant comments
- Unused imports/methodes
- Excessive logging
- Duplicate validations
- Boilerplate code
- Dead code paths