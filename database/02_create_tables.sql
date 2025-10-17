-- PostgreSQL Tables Setup voor BA Totalisatie
-- Dit script maakt de tabellen aan gebaseerd op de JPA entities

-- Maak de ba_records tabel aan
CREATE TABLE ba_records (
    id BIGSERIAL PRIMARY KEY,
    kenmerk VARCHAR(255) NOT NULL,
    cijfer1 DOUBLE PRECISION,
    cijfer2 DOUBLE PRECISION,
    cijfer3 DOUBLE PRECISION,
    cijfer4 DOUBLE PRECISION,
    cijfer5 DOUBLE PRECISION,
    cijfer6 DOUBLE PRECISION
);

-- Maak de ba_totals tabel aan
CREATE TABLE ba_totals (
    id BIGSERIAL PRIMARY KEY,
    kenmerk VARCHAR(255) NOT NULL,
    totaal1 DOUBLE PRECISION,
    totaal2 DOUBLE PRECISION,
    totaal3 DOUBLE PRECISION,
    totaal4 DOUBLE PRECISION,
    totaal5 DOUBLE PRECISION,
    request_timestamp BIGINT
);

-- Maak indexen aan voor betere performance
CREATE INDEX idx_ba_records_kenmerk ON ba_records(kenmerk);
CREATE INDEX idx_ba_totals_kenmerk ON ba_totals(kenmerk);
CREATE UNIQUE INDEX idx_ba_totals_kenmerk_unique ON ba_totals(kenmerk);

-- Geef de gebruiker rechten op de nieuwe tabellen
GRANT ALL PRIVILEGES ON TABLE ba_records TO ba_user;
GRANT ALL PRIVILEGES ON TABLE ba_totals TO ba_user;
GRANT ALL PRIVILEGES ON SEQUENCE ba_records_id_seq TO ba_user;
GRANT ALL PRIVILEGES ON SEQUENCE ba_totals_id_seq TO ba_user;
