# BA Totalisatie Frontend

Frontend die DIRECT met PostgreSQL database communiceert.

## Starten
1. Zorg dat PostgreSQL draait met database `ba_totals_db`
2. Run `start.bat` of `node server.js`
3. Open browser naar: `http://localhost:3000`

## Functionaliteiten
- BA Records toevoegen, bewerken, verwijderen
- BA Totals berekenen en bekijken
- Directe database communicatie (geen Spring Boot nodig)

## Bestanden
- `index.html` - Frontend interface
- `app.js` - JavaScript functionaliteit  
- `server.js` - Node.js server voor database connectie
- `package.json` - Dependencies
- `start.bat` - Start script
