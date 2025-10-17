@echo off
echo ===========================================
echo BA TOTALS DATABASE SETUP
echo ===========================================
echo Dit script zet de complete database opnieuw op
echo.

REM Ga naar de project directory
cd /d "%~dp0.."

REM Zoek PostgreSQL bin directory (probeer verschillende versies)
set PSQL_PATH=
if exist "C:\Program Files\PostgreSQL\17\bin\psql.exe" (
    set PSQL_PATH=C:\Program Files\PostgreSQL\17\bin\psql.exe
) else if exist "C:\Program Files\PostgreSQL\16\bin\psql.exe" (
    set PSQL_PATH=C:\Program Files\PostgreSQL\16\bin\psql.exe
) else if exist "C:\Program Files\PostgreSQL\15\bin\psql.exe" (
    set PSQL_PATH=C:\Program Files\PostgreSQL\15\bin\psql.exe
) else if exist "C:\Program Files\PostgreSQL\14\bin\psql.exe" (
    set PSQL_PATH=C:\Program Files\PostgreSQL\14\bin\psql.exe
) else if exist "C:\Program Files\PostgreSQL\13\bin\psql.exe" (
    set PSQL_PATH=C:\Program Files\PostgreSQL\13\bin\psql.exe
)

if "%PSQL_PATH%"=="" (
    echo ❌ FOUT: PostgreSQL niet gevonden
    echo Geïnstalleerde versies: 13, 14, 15, 16, 17
    echo Controleer of PostgreSQL is geïnstalleerd
    pause
    exit /b 1
)

echo ✅ PostgreSQL gevonden: %PSQL_PATH%
echo.

REM Vraag wachtwoord één keer voor alle operaties
set /p POSTGRES_PASSWORD="Voer PostgreSQL wachtwoord in: "
set PGPASSWORD=%POSTGRES_PASSWORD%

echo.
echo ===========================================
echo STAP 1: VOLLEDIGE CLEANUP
echo ===========================================
echo Verwijder ALLE bestaande database elementen...

REM Verbreek alle actieve verbindingen naar de database
echo - Verbreek actieve verbindingen...
"%PSQL_PATH%" -U postgres -c "SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname = 'ba_totals_db' AND pid <> pg_backend_pid();" 2>nul

REM Verwijder database volledig (dit verwijdert ook alle objecten)
echo - Verwijder database ba_totals_db...
"%PSQL_PATH%" -U postgres -c "DROP DATABASE IF EXISTS ba_totals_db;" 2>nul

REM Verwijder gebruiker volledig (na database verwijdering)
echo - Verwijder gebruiker ba_user...
"%PSQL_PATH%" -U postgres -c "DROP USER IF EXISTS ba_user;" 2>nul

REM Controleer of gebruiker echt is verwijderd
echo - Controleer of gebruiker is verwijderd...
"%PSQL_PATH%" -U postgres -c "SELECT 1 FROM pg_user WHERE usename = 'ba_user';" | findstr "1" >nul
if %ERRORLEVEL% equ 0 (
    echo - Gebruiker bestaat nog, ontneem alle rechten en verwijder...
    REM Ontneem alle rechten van de gebruiker
    "%PSQL_PATH%" -U postgres -c "REVOKE ALL ON SCHEMA public FROM ba_user;" 2>nul
    "%PSQL_PATH%" -U postgres -c "REVOKE ALL ON ALL TABLES IN SCHEMA public FROM ba_user;" 2>nul
    "%PSQL_PATH%" -U postgres -c "REVOKE ALL ON ALL SEQUENCES IN SCHEMA public FROM ba_user;" 2>nul
    "%PSQL_PATH%" -U postgres -c "REVOKE ALL ON DATABASE ba_totals_db FROM ba_user;" 2>nul
    REM Probeer opnieuw te verwijderen
    "%PSQL_PATH%" -U postgres -c "DROP USER ba_user;" 2>nul
)

REM Wacht even om zeker te zijn dat alles is verwijderd
timeout /t 3 /nobreak >nul

echo ✅ Cleanup voltooid - alles verwijderd
echo.

echo ===========================================
echo STAP 2: DATABASE EN GEBRUIKER AANMAKEN
echo ===========================================
echo Maak database en gebruiker opnieuw aan...

REM Maak gebruiker aan
echo - Maak gebruiker ba_user aan...
"%PSQL_PATH%" -U postgres -c "CREATE USER ba_user WITH PASSWORD 'ba_pass';" 2>nul
if %ERRORLEVEL% neq 0 (
    echo - Gebruiker bestaat al, controleer of deze werkt...
    "%PSQL_PATH%" -U postgres -c "SELECT 1 FROM pg_user WHERE usename = 'ba_user';" | findstr "1" >nul
    if %ERRORLEVEL% neq 0 (
        echo ❌ FOUT: Gebruiker aanmaken gefaald
        goto :error
    ) else (
        echo - Gebruiker ba_user bestaat al, ga door...
    )
)

REM Maak database aan
echo - Maak database ba_totals_db aan...
"%PSQL_PATH%" -U postgres -c "CREATE DATABASE ba_totals_db WITH OWNER = ba_user ENCODING = 'UTF8' LC_COLLATE = 'Dutch_Netherlands.1252' LC_CTYPE = 'Dutch_Netherlands.1252';" 2>nul
if %ERRORLEVEL% neq 0 (
    echo - Database bestaat mogelijk al, controleer...
    "%PSQL_PATH%" -U postgres -c "SELECT 1 FROM pg_database WHERE datname = 'ba_totals_db';" | findstr "1" >nul
    if %ERRORLEVEL% neq 0 (
        echo ❌ FOUT: Database aanmaken gefaald
        goto :error
    ) else (
        echo - Database ba_totals_db bestaat al, ga door...
    )
)

REM Geef rechten
echo - Geef rechten aan ba_user...
"%PSQL_PATH%" -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE ba_totals_db TO ba_user;" 2>nul
"%PSQL_PATH%" -U postgres -c "GRANT ALL ON SCHEMA public TO ba_user;" 2>nul

echo ✅ Database en gebruiker aangemaakt
echo.

echo ===========================================
echo STAP 3: TABELLEN AANMAKEN
echo ===========================================
echo Maak tabellen aan in de database...

REM Maak ba_records tabel
echo - Maak ba_records tabel aan...
"%PSQL_PATH%" -U postgres -d ba_totals_db -c "CREATE TABLE ba_records (id BIGSERIAL PRIMARY KEY, kenmerk VARCHAR(255) NOT NULL, cijfer1 DOUBLE PRECISION, cijfer2 DOUBLE PRECISION, cijfer3 DOUBLE PRECISION, cijfer4 DOUBLE PRECISION, cijfer5 DOUBLE PRECISION, cijfer6 DOUBLE PRECISION);" 2>nul
if %ERRORLEVEL% neq 0 (
    echo ❌ FOUT: ba_records tabel aanmaken gefaald
    goto :error
)

REM Maak ba_totals tabel
echo - Maak ba_totals tabel aan...
"%PSQL_PATH%" -U postgres -d ba_totals_db -c "CREATE TABLE ba_totals (id BIGSERIAL PRIMARY KEY, kenmerk VARCHAR(255) NOT NULL, totaal1 DOUBLE PRECISION, totaal2 DOUBLE PRECISION, totaal3 DOUBLE PRECISION, totaal4 DOUBLE PRECISION, totaal5 DOUBLE PRECISION, request_timestamp BIGINT);" 2>nul
if %ERRORLEVEL% neq 0 (
    echo ❌ FOUT: ba_totals tabel aanmaken gefaald
    goto :error
)

REM Maak indexen
echo - Maak indexen aan...
"%PSQL_PATH%" -U postgres -d ba_totals_db -c "CREATE INDEX idx_ba_records_kenmerk ON ba_records(kenmerk);" 2>nul
"%PSQL_PATH%" -U postgres -d ba_totals_db -c "CREATE INDEX idx_ba_totals_kenmerk ON ba_totals(kenmerk);" 2>nul

REM Geef rechten op tabellen
echo - Geef rechten op tabellen...
"%PSQL_PATH%" -U postgres -d ba_totals_db -c "GRANT ALL PRIVILEGES ON TABLE ba_records TO ba_user;" 2>nul
"%PSQL_PATH%" -U postgres -d ba_totals_db -c "GRANT ALL PRIVILEGES ON TABLE ba_totals TO ba_user;" 2>nul
"%PSQL_PATH%" -U postgres -d ba_totals_db -c "GRANT ALL PRIVILEGES ON SEQUENCE ba_records_id_seq TO ba_user;" 2>nul
"%PSQL_PATH%" -U postgres -d ba_totals_db -c "GRANT ALL PRIVILEGES ON SEQUENCE ba_totals_id_seq TO ba_user;" 2>nul

echo ✅ Tabellen aangemaakt
echo.

echo ===========================================
echo STAP 4: SAMPLE DATA TOEVOEGEN
echo ===========================================
echo Voeg test data toe...

REM Voeg sample data toe
echo - Voeg sample BA records toe...
"%PSQL_PATH%" -U postgres -d ba_totals_db -c "INSERT INTO ba_records (kenmerk, cijfer1, cijfer2, cijfer3, cijfer4, cijfer5, cijfer6) VALUES ('BA001', 100.50, 200.75, 300.25, 400.00, 500.50, 250.25), ('BA001', 150.25, 250.50, 350.75, 450.25, 550.00, 300.50), ('BA001', 200.00, 300.25, 400.50, 500.75, 600.25, 350.75), ('BA002', 75.50, 175.25, 275.00, 375.50, 475.75, 200.00), ('BA002', 125.75, 225.00, 325.25, 425.50, 525.75, 275.25), ('BA003', 300.00, 400.25, 500.50, 600.75, 700.00, 400.50);" 2>nul
if %ERRORLEVEL% neq 0 (
    echo ❌ FOUT: Sample data toevoegen gefaald
    goto :error
)

echo - Voeg sample BA totals toe...
"%PSQL_PATH%" -U postgres -d ba_totals_db -c "INSERT INTO ba_totals (kenmerk, totaal1, totaal2, totaal3, totaal4, totaal5) VALUES ('BA001', 450.75, 751.50, 1051.50, 1351.00, 1650.75), ('BA002', 201.25, 400.25, 600.25, 801.00, 1001.50), ('BA003', 300.00, 400.25, 500.50, 600.75, 700.00);" 2>nul
if %ERRORLEVEL% neq 0 (
    echo ❌ FOUT: Sample totals toevoegen gefaald
    goto :error
)

echo ✅ Sample data toegevoegd
echo.

echo ===========================================
echo STAP 5: VERBINDING TESTEN
echo ===========================================
echo Test verbinding met applicatie gebruiker...

set PGPASSWORD=ba_pass
"%PSQL_PATH%" -U ba_user -d ba_totals_db -c "SELECT COUNT(*) as ba_records_count FROM ba_records;" 2>nul
if %ERRORLEVEL% neq 0 (
    echo ❌ FOUT: Verbinding met ba_user gefaald
    goto :error
) else (
    echo ✅ Verbinding met ba_user succesvol
)

echo.
echo ===========================================
echo SETUP VOLTOOID!
echo ===========================================
echo.
echo Database 'ba_totals_db' is succesvol opgezet:
echo - Gebruiker: ba_user
echo - Wachtwoord: ba_pass
echo - Tabellen: ba_records, ba_totals
echo - Sample data: 6 records in ba_records, 3 records in ba_totals
echo.
echo Je kunt nu de applicatie starten met: .\mvnd.bat spring-boot:run
echo.
goto :end

:error
echo.
echo ===========================================
echo FOUT OPGETREDEN
echo ===========================================
echo Er is een fout opgetreden tijdens de setup.
echo Controleer de PostgreSQL verbinding en probeer opnieuw.
echo.

:end
REM Wis wachtwoord uit geheugen
set PGPASSWORD=
set POSTGRES_PASSWORD=
pause
