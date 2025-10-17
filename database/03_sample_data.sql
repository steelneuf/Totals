-- Sample Data voor BA Totalisatie
-- Dit script voegt test data toe aan de tabellen

-- Voeg sample BA records toe
INSERT INTO ba_records (kenmerk, cijfer1, cijfer2, cijfer3, cijfer4, cijfer5, cijfer6) VALUES
('BA001', 100.50, 200.75, 300.25, 400.00, 500.50, 250.25),
('BA001', 150.25, 250.50, 350.75, 450.25, 550.00, 300.50),
('BA001', 200.00, 300.25, 400.50, 500.75, 600.25, 350.75),
('BA002', 75.50, 175.25, 275.00, 375.50, 475.75, 200.00),
('BA002', 125.75, 225.00, 325.25, 425.50, 525.75, 275.25),
('BA003', 300.00, 400.25, 500.50, 600.75, 700.00, 400.50);

-- Voeg sample BA totals toe (deze worden normaal gesproken berekend door de applicatie)
INSERT INTO ba_totals (kenmerk, totaal1, totaal2, totaal3, totaal4, totaal5) VALUES
('BA001', 450.75, 751.50, 1051.50, 1351.00, 1650.75),
('BA002', 201.25, 400.25, 600.25, 801.00, 1001.50),
('BA003', 300.00, 400.25, 500.50, 600.75, 700.00);
