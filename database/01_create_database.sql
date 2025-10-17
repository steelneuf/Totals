-- PostgreSQL Database Setup voor BA Totalisatie
-- Dit script maakt de database aan

-- Maak de database aan (als deze nog niet bestaat)
CREATE DATABASE ba_totals_db
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'Dutch_Netherlands.1252'
    LC_CTYPE = 'Dutch_Netherlands.1252'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;

-- Maak een specifieke gebruiker aan voor de applicatie
CREATE USER ba_user WITH PASSWORD 'ba_pass';

-- Geef de gebruiker rechten op de database
GRANT ALL PRIVILEGES ON DATABASE ba_totals_db TO ba_user;

-- Geef de gebruiker rechten op het public schema
GRANT ALL ON SCHEMA public TO ba_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO ba_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO ba_user;
