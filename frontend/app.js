// API Base URL - Node.js server die direct met database communiceert
const API_BASE_URL = 'http://localhost:3000';

// Spring Boot backend URL voor calculate totals endpoint
const SPRING_BOOT_URL = 'http://localhost:8080';

// Global variables
let records = [];
let totals = [];
let editingRecordId = null;

// Initialize the application
document.addEventListener('DOMContentLoaded', function() {
    loadRecords();
    loadTotals();
    setupEventListeners();
});

// Setup event listeners
function setupEventListeners() {
    document.getElementById('recordFormElement').addEventListener('submit', handleRecordSubmit);
    document.getElementById('cancelBtn').addEventListener('click', cancelEdit);
}

// Show message to user
function showMessage(message, type = 'success') {
    const messageContainer = document.getElementById('messageContainer');
    const messageDiv = document.createElement('div');
    messageDiv.className = `message ${type}`;
    messageDiv.textContent = message;
    messageContainer.appendChild(messageDiv);
    
    // Remove message after 5 seconds
    setTimeout(() => {
        messageDiv.remove();
    }, 5000);
}

// Load BA Records
async function loadRecords() {
    const loading = document.getElementById('recordsLoading');
    const table = document.getElementById('recordsTableElement');
    
    loading.classList.remove('hidden');
    table.classList.add('hidden');
    
    try {
        const response = await fetch(`${API_BASE_URL}/api/records`);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        records = await response.json();
        
        displayRecords();
        loading.classList.add('hidden');
        table.classList.remove('hidden');
    } catch (error) {
        console.error('Error loading records:', error);
        showMessage('Fout bij laden van records: ' + error.message, 'error');
        loading.classList.add('hidden');
    }
}

// Display records in table (only first 10)
function displayRecords() {
    const tbody = document.getElementById('recordsTableBody');
    tbody.innerHTML = '';
    
    // Show only first 10 records
    const recordsToShow = records.slice(0, 10);
    
    recordsToShow.forEach(record => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${record.id || ''}</td>
            <td>${record.kenmerk || ''}</td>
            <td>${record.cijfer1 ? parseFloat(record.cijfer1).toFixed(2) : ''}</td>
            <td>${record.cijfer2 ? parseFloat(record.cijfer2).toFixed(2) : ''}</td>
            <td>${record.cijfer3 ? parseFloat(record.cijfer3).toFixed(2) : ''}</td>
            <td>${record.cijfer4 ? parseFloat(record.cijfer4).toFixed(2) : ''}</td>
            <td>${record.cijfer5 ? parseFloat(record.cijfer5).toFixed(2) : ''}</td>
            <td>${record.cijfer6 ? parseFloat(record.cijfer6).toFixed(2) : ''}</td>
            <td>
                <button class="edit" onclick="editRecord(${record.id})">Bewerken</button>
                <button class="delete" onclick="deleteRecord(${record.id})">Verwijderen</button>
            </td>
        `;
        tbody.appendChild(row);
    });
    
    // Show total count if there are more than 10 records
    if (records.length > 10) {
        const infoRow = document.createElement('tr');
        infoRow.innerHTML = `
            <td colspan="9" style="text-align: center; font-style: italic; background-color: #f0f0f0;">
                Toont eerste 10 van ${records.length} records. Gebruik "Ververs" om bij te werken.
            </td>
        `;
        tbody.appendChild(infoRow);
    }
}

// Handle record form submission
function handleRecordSubmit(event) {
    event.preventDefault();
    
    const formData = {
        id: document.getElementById('recordId').value,
        kenmerk: document.getElementById('kenmerk').value,
        cijfer1: parseFloat(document.getElementById('cijfer1').value) || null,
        cijfer2: parseFloat(document.getElementById('cijfer2').value) || null,
        cijfer3: parseFloat(document.getElementById('cijfer3').value) || null,
        cijfer4: parseFloat(document.getElementById('cijfer4').value) || null,
        cijfer5: parseFloat(document.getElementById('cijfer5').value) || null,
        cijfer6: parseFloat(document.getElementById('cijfer6').value) || null
    };
    
    if (editingRecordId) {
        updateRecord(formData);
    } else {
        addRecord(formData);
    }
}

// Add new record
async function addRecord(recordData) {
    try {
        const response = await fetch(`${API_BASE_URL}/api/records`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(recordData)
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const newRecord = await response.json();
        showMessage('Record succesvol toegevoegd');
        resetForm();
        loadRecords();
    } catch (error) {
        console.error('Error adding record:', error);
        showMessage('Fout bij toevoegen van record: ' + error.message, 'error');
    }
}

// Update existing record
async function updateRecord(recordData) {
    try {
        const response = await fetch(`${API_BASE_URL}/api/records/${editingRecordId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(recordData)
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const updatedRecord = await response.json();
        showMessage('Record succesvol bijgewerkt');
        resetForm();
        loadRecords();
    } catch (error) {
        console.error('Error updating record:', error);
        showMessage('Fout bij bijwerken van record: ' + error.message, 'error');
    }
}

// Edit record
function editRecord(id) {
    const record = records.find(r => r.id == id);
    if (record) {
        editingRecordId = id;
        document.getElementById('recordId').value = record.id;
        document.getElementById('kenmerk').value = record.kenmerk || '';
        document.getElementById('cijfer1').value = record.cijfer1 || '';
        document.getElementById('cijfer2').value = record.cijfer2 || '';
        document.getElementById('cijfer3').value = record.cijfer3 || '';
        document.getElementById('cijfer4').value = record.cijfer4 || '';
        document.getElementById('cijfer5').value = record.cijfer5 || '';
        document.getElementById('cijfer6').value = record.cijfer6 || '';
        
        document.getElementById('formTitle').textContent = 'BA Record Bewerken';
        document.getElementById('submitBtn').textContent = 'Bijwerken';
        document.getElementById('cancelBtn').classList.remove('hidden');
    }
}

// Delete record
async function deleteRecord(id) {
    if (confirm('Weet je zeker dat je dit record wilt verwijderen?')) {
        try {
            const response = await fetch(`${API_BASE_URL}/api/records/${id}`, {
                method: 'DELETE'
            });
            
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            
            showMessage('Record succesvol verwijderd');
            loadRecords();
        } catch (error) {
            console.error('Error deleting record:', error);
            showMessage('Fout bij verwijderen van record: ' + error.message, 'error');
        }
    }
}

// Cancel edit
function cancelEdit() {
    resetForm();
}

// Reset form
function resetForm() {
    editingRecordId = null;
    document.getElementById('recordFormElement').reset();
    document.getElementById('recordId').value = '';
    document.getElementById('formTitle').textContent = 'Nieuwe BA Record Toevoegen';
    document.getElementById('submitBtn').textContent = 'Opslaan';
    document.getElementById('cancelBtn').classList.add('hidden');
}

// Load BA Totals
async function loadTotals() {
    const loading = document.getElementById('totalsLoading');
    const table = document.getElementById('totalsTable');
    
    loading.classList.remove('hidden');
    table.classList.add('hidden');
    
    try {
        const response = await fetch(`${API_BASE_URL}/api/totals`);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        totals = await response.json();
        
        displayTotals();
        loading.classList.add('hidden');
        table.classList.remove('hidden');
    } catch (error) {
        console.error('Error loading totals:', error);
        showMessage('Fout bij laden van totalen: ' + error.message, 'error');
        loading.classList.add('hidden');
    }
}

// Display totals in table
function displayTotals() {
    const tbody = document.getElementById('totalsTableBody');
    tbody.innerHTML = '';
    
    totals.forEach(total => {
        const row = document.createElement('tr');
        
        // Format timestamp for display
        let formattedTimestamp = '';
        if (total.updatedAt) {
            const date = new Date(total.updatedAt);
            formattedTimestamp = date.toLocaleString('nl-NL', {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit',
                hour: '2-digit',
                minute: '2-digit',
                second: '2-digit'
            });
        }
        
        row.innerHTML = `
            <td>${total.id || ''}</td>
            <td>${total.kenmerk || ''}</td>
            <td>${total.totaal1 ? parseFloat(total.totaal1).toFixed(2) : ''}</td>
            <td>${total.totaal2 ? parseFloat(total.totaal2).toFixed(2) : ''}</td>
            <td>${total.totaal3 ? parseFloat(total.totaal3).toFixed(2) : ''}</td>
            <td>${total.totaal4 ? parseFloat(total.totaal4).toFixed(2) : ''}</td>
            <td>${total.totaal5 ? parseFloat(total.totaal5).toFixed(2) : ''}</td>
            <td>${formattedTimestamp}</td>
            <td>
                <button class="delete" onclick="deleteTotal(${total.id})">Verwijderen</button>
            </td>
        `;
        tbody.appendChild(row);
    });
}

// Calculate totals for a specific kenmerk
async function calculateTotals() {
    const kenmerk = prompt('Voer het kenmerk in waarvoor je totalen wilt berekenen:');
    if (!kenmerk) return;
    
    const bijdragePercentage = document.getElementById('bijdragePercentage').value;
    if (!bijdragePercentage || bijdragePercentage < 1 || bijdragePercentage > 100) {
        showMessage('Bijdragepercentage moet een geheel getal zijn tussen 1 en 100', 'error');
        return;
    }
    
    try {
        const response = await fetch(`${SPRING_BOOT_URL}/api/v1/ba/${encodeURIComponent(kenmerk)}/calculate-totals?bijdragePercentage=${bijdragePercentage}`, {
            method: 'POST'
        });
        
        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || `HTTP error! status: ${response.status}`);
        }
        
        const result = await response.text();
        showMessage(result);
        loadTotals();
        
    } catch (error) {
        console.error('Error calculating totals:', error);
        showMessage('Fout bij berekenen van totalen: ' + error.message, 'error');
    }
}

// Delete single total
async function deleteTotal(id) {
    if (confirm('Weet je zeker dat je dit totaal wilt verwijderen?')) {
        try {
            const response = await fetch(`${API_BASE_URL}/api/totals/${id}`, {
                method: 'DELETE'
            });
            
            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.error || `HTTP error! status: ${response.status}`);
            }
            
            showMessage('Totaal succesvol verwijderd');
            loadTotals();
        } catch (error) {
            console.error('Error deleting total:', error);
            showMessage('Fout bij verwijderen van totaal: ' + error.message, 'error');
        }
    }
}

// Delete all totals
async function deleteAllTotals() {
    if (confirm('Weet je zeker dat je ALLE totalen wilt verwijderen? Deze actie kan niet ongedaan worden gemaakt!')) {
        try {
            const response = await fetch(`${API_BASE_URL}/api/totals`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            
            if (!response.ok) {
                let errorMessage = `HTTP error! status: ${response.status}`;
                try {
                    const errorData = await response.json();
                    errorMessage = errorData.error || errorMessage;
                } catch (jsonError) {
                    // If response is not JSON, get text
                    const textResponse = await response.text();
                    console.error('Non-JSON response:', textResponse);
                    errorMessage = `Server error: ${textResponse.substring(0, 100)}...`;
                }
                throw new Error(errorMessage);
            }
            
            const result = await response.json();
            showMessage(`Alle totalen succesvol verwijderd (${result.deletedCount || 0} totalen)`);
            loadTotals();
        } catch (error) {
            console.error('Error deleting all totals:', error);
            showMessage('Fout bij verwijderen van alle totalen: ' + error.message, 'error');
        }
    }
}

// Database status check
async function checkDatabaseStatus() {
    try {
        const response = await fetch(`${API_BASE_URL}/api/records`);
        if (response.ok) {
            showMessage('Database verbinding succesvol!', 'success');
        } else {
            showMessage('Database verbinding gefaald!', 'error');
        }
    } catch (error) {
        showMessage('Database niet bereikbaar: ' + error.message, 'error');
    }
}

// Bulk insert 10000 records with same kenmerk
async function bulkInsertRecords() {
    const kenmerk = prompt('Voer het kenmerk in voor de 10.000 records:');
    if (!kenmerk) return;
    
    const count = prompt('Aantal records (standaard 10000):', '10000');
    const recordCount = parseInt(count) || 10000;
    
    if (recordCount > 50000) {
        if (!confirm(`Je wilt ${recordCount} records toevoegen. Dit kan lang duren. Doorgaan?`)) {
            return;
        }
    }
    
    try {
        showMessage(`Bezig met toevoegen van ${recordCount} records...`, 'success');
        
        const response = await fetch(`${API_BASE_URL}/api/records/bulk`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                kenmerk: kenmerk,
                count: recordCount
            })
        });
        
        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.error || `HTTP error! status: ${response.status}`);
        }
        
        const result = await response.json();
        showMessage(result.message, 'success');
        loadRecords(); // Refresh the records table
        
    } catch (error) {
        console.error('Error bulk inserting records:', error);
        showMessage('Fout bij toevoegen van records: ' + error.message, 'error');
    }
}

// Delete all records
async function deleteAllRecords() {
    if (confirm('Weet je zeker dat je ALLE records wilt verwijderen? Deze actie kan niet ongedaan worden gemaakt!')) {
        try {
            const response = await fetch(`${API_BASE_URL}/api/records`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            
            if (!response.ok) {
                let errorMessage = `HTTP error! status: ${response.status}`;
                try {
                    const errorData = await response.json();
                    errorMessage = errorData.error || errorMessage;
                } catch (jsonError) {
                    const textResponse = await response.text();
                    console.error('Non-JSON response:', textResponse);
                    errorMessage = `Server error: ${textResponse.substring(0, 100)}...`;
                }
                throw new Error(errorMessage);
            }
            
            const result = await response.json();
            showMessage(`Alle records succesvol verwijderd (${result.deletedCount || 0} records)`, 'success');
            loadRecords(); // Refresh the records table
            
        } catch (error) {
            console.error('Error deleting all records:', error);
            showMessage('Fout bij verwijderen van alle records: ' + error.message, 'error');
        }
    }
}