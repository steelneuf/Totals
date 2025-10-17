-- Cleanup Script voor BA Totalisatie Database
-- Dit script verwijdert alle data en tabellen (gebruik met voorzichtigheid!)

-- Verwijder alle data uit de tabellen
TRUNCATE TABLE ba_records CASCADE;
TRUNCATE TABLE ba_totals CASCADE;

-- Of verwijder de tabellen volledig (uncomment indien gewenst)
-- DROP TABLE IF EXISTS ba_records CASCADE;
-- DROP TABLE IF EXISTS ba_totals CASCADE;

-- Of verwijder de hele database (uncomment indien gewenst)
-- \c postgres;
-- DROP DATABASE IF EXISTS ba_totals_db;
-- DROP USER IF EXISTS ba_user;
