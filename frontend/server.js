const express = require('express');
const cors = require('cors');
const { Pool } = require('pg');

const app = express();
const port = 3000;

// Database configuratie
const pool = new Pool({
  user: 'ba_user',
  host: 'localhost',
  database: 'ba_totals_db',
  password: 'ba_pass',
  port: 5432,
});

// Middleware
app.use(cors());
app.use(express.json());
app.use(express.static('.'));

// Test database connectie
pool.on('connect', () => {
  console.log('Verbonden met PostgreSQL database');
});

pool.on('error', (err) => {
  console.error('Database connectie fout:', err);
});

// Routes

// Get all BA records
app.get('/api/records', async (req, res) => {
  try {
    const result = await pool.query('SELECT * FROM ba_records ORDER BY id');
    res.json(result.rows);
  } catch (err) {
    console.error('Error fetching records:', err);
    res.status(500).json({ error: 'Database error' });
  }
});

// Get BA record by ID
app.get('/api/records/:id', async (req, res) => {
  try {
    const { id } = req.params;
    const result = await pool.query('SELECT * FROM ba_records WHERE id = $1', [id]);
    
    if (result.rows.length === 0) {
      return res.status(404).json({ error: 'Record not found' });
    }
    
    res.json(result.rows[0]);
  } catch (err) {
    console.error('Error fetching record:', err);
    res.status(500).json({ error: 'Database error' });
  }
});

// Create new BA record
app.post('/api/records', async (req, res) => {
  try {
    const { kenmerk, cijfer1, cijfer2, cijfer3, cijfer4, cijfer5, cijfer6 } = req.body;
    
    const result = await pool.query(
      'INSERT INTO ba_records (kenmerk, cijfer1, cijfer2, cijfer3, cijfer4, cijfer5, cijfer6) VALUES ($1, $2, $3, $4, $5, $6, $7) RETURNING *',
      [kenmerk, cijfer1, cijfer2, cijfer3, cijfer4, cijfer5, cijfer6]
    );
    
    res.status(201).json(result.rows[0]);
  } catch (err) {
    console.error('Error creating record:', err);
    res.status(500).json({ error: 'Database error' });
  }
});

// Update BA record
app.put('/api/records/:id', async (req, res) => {
  try {
    const { id } = req.params;
    const { kenmerk, cijfer1, cijfer2, cijfer3, cijfer4, cijfer5, cijfer6 } = req.body;
    
    const result = await pool.query(
      'UPDATE ba_records SET kenmerk = $1, cijfer1 = $2, cijfer2 = $3, cijfer3 = $4, cijfer4 = $5, cijfer5 = $6, cijfer6 = $7 WHERE id = $8 RETURNING *',
      [kenmerk, cijfer1, cijfer2, cijfer3, cijfer4, cijfer5, cijfer6, id]
    );
    
    if (result.rows.length === 0) {
      return res.status(404).json({ error: 'Record not found' });
    }
    
    res.json(result.rows[0]);
  } catch (err) {
    console.error('Error updating record:', err);
    res.status(500).json({ error: 'Database error' });
  }
});

// Delete BA record
app.delete('/api/records/:id', async (req, res) => {
  try {
    const { id } = req.params;
    const result = await pool.query('DELETE FROM ba_records WHERE id = $1 RETURNING *', [id]);
    
    if (result.rows.length === 0) {
      return res.status(404).json({ error: 'Record not found' });
    }
    
    res.json({ message: 'Record deleted successfully' });
  } catch (err) {
    console.error('Error deleting record:', err);
    res.status(500).json({ error: 'Database error' });
  }
});

// Get all BA totals
app.get('/api/totals', async (req, res) => {
  try {
    const result = await pool.query('SELECT * FROM ba_totals ORDER BY id');
    res.json(result.rows);
  } catch (err) {
    console.error('Error fetching totals:', err);
    res.status(500).json({ error: 'Database error' });
  }
});

// Calculate totals for a kenmerk
app.post('/api/totals/calculate/:kenmerk', async (req, res) => {
    try {
        const { kenmerk } = req.params;
        const { bijdragePercentage } = req.query;
        
        // Validatie van bijdragePercentage
        if (!bijdragePercentage || isNaN(bijdragePercentage) || bijdragePercentage < 1 || bijdragePercentage > 100) {
            return res.status(400).json({ error: 'Bijdragepercentage moet een geheel getal zijn tussen 1 en 100' });
        }
        
        const bijdrageInt = parseInt(bijdragePercentage);
        
        // Get all records for this kenmerk
        const recordsResult = await pool.query('SELECT * FROM ba_records WHERE kenmerk = $1', [kenmerk]);
        const records = recordsResult.rows;
        
        if (records.length === 0) {
            return res.status(400).json({ error: `Geen records gevonden voor kenmerk: ${kenmerk}` });
        }
        
        // Calculate totals
        let totaal1 = 0, totaal2 = 0, totaal3 = 0, totaal4 = 0, totaal5 = 0, totaal6 = 0;
        
        records.forEach(record => {
            if (record.cijfer1) totaal1 += parseFloat(record.cijfer1);
            if (record.cijfer2) totaal2 += parseFloat(record.cijfer2);
            if (record.cijfer3) totaal3 += parseFloat(record.cijfer3);
            if (record.cijfer4) totaal4 += parseFloat(record.cijfer4);
            if (record.cijfer5) totaal5 += parseFloat(record.cijfer5);
            if (record.cijfer6) totaal6 += parseFloat(record.cijfer6);
        });
        
        // Converteer bijdragepercentage naar decimaal voor berekeningen
        const bijdrageDecimal = bijdrageInt / 100.0;
        
        // Voer de berekeningen uit zoals in de Java backend
        let cijfer7, cijfer1_output;
        
        if (totaal1 > (totaal2 / bijdrageDecimal)) {
            cijfer7 = totaal1;
            cijfer1_output = totaal2 / bijdrageDecimal;
        } else {
            cijfer7 = totaal1;
            cijfer1_output = totaal1;
        }
        
        // Berekening 2: cijfer8 = (cijfer3 / cijfer7) * cijfer1_output
        const cijfer8 = (totaal3 / cijfer7) * cijfer1_output;
        
        // Berekening 3: cijfer9 = (cijfer4 / cijfer7) * cijfer1_output
        const cijfer9 = (totaal4 / cijfer7) * cijfer1_output;
        
        // Berekening 4: cijfer5_output = totaal5 (normale sum)
        const cijfer5_output = totaal5;
        
        // Berekening 5: cijfer6_output = totaal6 (normale sum)
        const cijfer6_output = totaal6;
        
        // Check if total already exists
        const existingTotal = await pool.query('SELECT * FROM ba_totals WHERE kenmerk = $1', [kenmerk]);
        
        let result;
        if (existingTotal.rows.length > 0) {
            // Update existing total
            result = await pool.query(
                'UPDATE ba_totals SET totaal1 = $1, totaal2 = $2, totaal3 = $3, totaal4 = $4, totaal5 = $5 WHERE kenmerk = $6 RETURNING *',
                [cijfer1_output, cijfer8, cijfer9, cijfer5_output, cijfer6_output, kenmerk]
            );
        } else {
            // Create new total
            result = await pool.query(
                'INSERT INTO ba_totals (kenmerk, totaal1, totaal2, totaal3, totaal4, totaal5) VALUES ($1, $2, $3, $4, $5, $6) RETURNING *',
                [kenmerk, cijfer1_output, cijfer8, cijfer9, cijfer5_output, cijfer6_output]
            );
        }
        
        res.json({
            message: `Totalen berekend voor kenmerk: ${kenmerk} met bijdragepercentage: ${bijdrageInt}%`,
            recordsProcessed: records.length,
            total: result.rows[0]
        });
    } catch (err) {
        console.error('Error calculating totals:', err);
        res.status(500).json({ error: 'Database error' });
    }
});

// Delete BA total by ID
app.delete('/api/totals/:id', async (req, res) => {
    try {
        const { id } = req.params;
        const result = await pool.query('DELETE FROM ba_totals WHERE id = $1 RETURNING *', [id]);
        
        if (result.rows.length === 0) {
            return res.status(404).json({ error: 'Total not found' });
        }
        
        res.json({ message: 'Total deleted successfully' });
    } catch (err) {
        console.error('Error deleting total:', err);
        res.status(500).json({ error: 'Database error' });
    }
});

// Delete all totals
app.delete('/api/totals', async (req, res) => {
    try {
        console.log('Deleting all totals...');
        const result = await pool.query('DELETE FROM ba_totals');
        console.log(`Deleted ${result.rowCount} totals`);
        res.json({ 
            message: 'All totals deleted successfully',
            deletedCount: result.rowCount 
        });
    } catch (err) {
        console.error('Error deleting all totals:', err);
        res.status(500).json({ error: 'Database error: ' + err.message });
    }
});

// Bulk insert 10000 records with same kenmerk
app.post('/api/records/bulk', async (req, res) => {
    try {
        const { kenmerk, count = 10000 } = req.body;
        
        if (!kenmerk) {
            return res.status(400).json({ error: 'Kenmerk is verplicht' });
        }
        
        console.log(`Creating ${count} records with kenmerk: ${kenmerk}`);
        
        // Use batch insert for better performance
        const client = await pool.connect();
        try {
            await client.query('BEGIN');
            
            // Insert in batches of 100 for better performance and simpler SQL
            const batchSize = 100;
            let totalInserted = 0;
            
            for (let i = 0; i < count; i += batchSize) {
                const currentBatchSize = Math.min(batchSize, count - i);
                const values = [];
                const placeholders = [];
                
                for (let j = 0; j < currentBatchSize; j++) {
                    const baseIndex = j * 7;
                    placeholders.push(`($${baseIndex + 1}, $${baseIndex + 2}, $${baseIndex + 3}, $${baseIndex + 4}, $${baseIndex + 5}, $${baseIndex + 6}, $${baseIndex + 7})`);
                    
                    // Validatie regels voor bulk insert:
                    // 1. Cijfer 1 is altijd minimaal 2x groter dan cijfer 2
                    // 2. Cijfer 3 + cijfer 4 = cijfer 1
                    // 3. Cijfer 5 en cijfer 6 blijven random
                    
                    // Genereer cijfer 2 eerst (random tussen 0-500)
                    const cijfer2 = Math.round((Math.random() * 500) * 100) / 100;
                    
                    // Bereken cijfer 1 = cijfer 2 * (2 + random factor) om minimaal 2x groter te garanderen
                    const multiplier = 2 + Math.random() * 3; // 2.0 tot 5.0
                    const cijfer1 = Math.round((cijfer2 * multiplier) * 100) / 100;
                    
                    // Genereer cijfer 3 en cijfer 4 zodat ze samen cijfer 1 vormen
                    const cijfer3Ratio = Math.random(); // 0 tot 1
                    const cijfer3 = Math.round((cijfer1 * cijfer3Ratio) * 100) / 100;
                    const cijfer4 = Math.round((cijfer1 - cijfer3) * 100) / 100;
                    
                    // Cijfer 5 en 6 blijven random (0-1000)
                    const cijfer5 = Math.round((Math.random() * 1000) * 100) / 100;
                    const cijfer6 = Math.round((Math.random() * 1000) * 100) / 100;
                    
                    values.push(
                        kenmerk,
                        cijfer1,
                        cijfer2,
                        cijfer3,
                        cijfer4,
                        cijfer5,
                        cijfer6
                    );
                }
                
                await client.query(
                    `INSERT INTO ba_records (kenmerk, cijfer1, cijfer2, cijfer3, cijfer4, cijfer5, cijfer6) VALUES ${placeholders.join(',')}`,
                    values
                );
                
                totalInserted += currentBatchSize;
                console.log(`Inserted ${totalInserted}/${count} records...`);
            }
            
            await client.query('COMMIT');
            
            res.json({
                message: `${totalInserted} records succesvol toegevoegd met kenmerk: ${kenmerk}`,
                recordsCreated: totalInserted
            });
        } catch (err) {
            await client.query('ROLLBACK');
            throw err;
        } finally {
            client.release();
        }
    } catch (err) {
        console.error('Error creating bulk records:', err);
        res.status(500).json({ error: 'Database error: ' + err.message });
    }
});

// Delete all records
app.delete('/api/records', async (req, res) => {
    try {
        console.log('Deleting all records...');
        const result = await pool.query('DELETE FROM ba_records');
        console.log(`Deleted ${result.rowCount} records`);
        res.json({ 
            message: 'Alle records succesvol verwijderd',
            deletedCount: result.rowCount 
        });
    } catch (err) {
        console.error('Error deleting all records:', err);
        res.status(500).json({ error: 'Database error: ' + err.message });
    }
});

// Start server
app.listen(port, () => {
  console.log(`BA Totalisatie Frontend server running on http://localhost:${port}`);
  console.log('Database: PostgreSQL (ba_totals_db)');
});
    