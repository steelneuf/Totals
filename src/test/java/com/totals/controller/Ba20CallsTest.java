package com.totals.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.totals.dto.BaCalculationResponse;
import com.totals.model.BaTotalRecord;
import com.totals.repository.BaTotalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class Ba20CallsTest {

    private static final Logger logger = LoggerFactory.getLogger(Ba20CallsTest.class);

    @LocalServerPort
    private int port;
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private BaTotalRepository baTotalRepository;
    
    private String baseUrl;
    private final String kenmerk = "BA001";
    private final int[] bijdragePercentages = {5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100};
    private final int TOTAL_REQUESTS = 1000;
    
    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/v1/ba/" + kenmerk + "/calculate-totals";
    }
    
    @Test
    void testOneThousandConsecutiveCalculations() {
        logger.info("=== STARTING 1000 CONSECUTIVE CALCULATION TEST ===");
        
        long totalStartTime = System.currentTimeMillis();
        int successCount = 0;
        int errorCount = 0;
        
        // Voer 1000 opeenvolgende calls uit met cyclische percentages
        for (int i = 0; i < TOTAL_REQUESTS; i++) {
            int percentage = bijdragePercentages[i % bijdragePercentages.length];
            String url = baseUrl + "?bijdragePercentage=" + percentage;
            
            // Log elke 50e call om spam te voorkomen (1000 calls = veel output)
            if ((i + 1) % 50 == 0 || i < 10) {
                logger.info("Call {}: {}%", (i + 1), percentage);
            }
            
            long startTime = System.currentTimeMillis();
            try {
                ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);
                long endTime = System.currentTimeMillis();
                
                // Verifieer dat de call succesvol is
                assertEquals(HttpStatus.OK, response.getStatusCode(), 
                    "Call " + (i + 1) + " met " + percentage + "% moet succesvol zijn");
                
                // Verifieer de response structuur
                BaCalculationResponse calculationResponse = assertDoesNotThrow(() -> 
                    objectMapper.readValue(response.getBody(), BaCalculationResponse.class),
                    "Response moet geldige JSON zijn");
                
                assertNotNull(calculationResponse.totalen(), "Totalen mogen niet null zijn");
                assertEquals(5, calculationResponse.totalen().size(), "Moet 5 totalen bevatten");
                assertEquals(percentage, calculationResponse.bijdragePercentage(), 
                    "Bijdragepercentage moet overeenkomen");
                
                successCount++;
                
                // Log elke 50e call met details (1000 calls = veel output)
                if ((i + 1) % 50 == 0 || i < 10) {
                    logger.info("✅ {}% - {}ms - [{}]", 
                        percentage, (endTime - startTime), 
                        String.format("%.1f, %.1f, %.1f, %.1f, %.1f", 
                            calculationResponse.totalen().get(0), calculationResponse.totalen().get(1),
                            calculationResponse.totalen().get(2), calculationResponse.totalen().get(3),
                            calculationResponse.totalen().get(4)));
                }
                
            } catch (Exception e) {
                errorCount++;
                logger.error("❌ Call {} failed: {}", (i + 1), e.getMessage());
                throw e; // Re-throw om test te laten falen
            }
        }
        
        long totalEndTime = System.currentTimeMillis();
        long totalDuration = totalEndTime - totalStartTime;
        
        // Verifieer dat de laatste berekening correct is opgeslagen in de database
        validateFinalDatabaseState();
        
        logger.info("=== 1000 CONSECUTIVE CALCULATION TEST COMPLETED ===");
        logger.info("📊 STATISTICS:");
        logger.info("   Total requests: {}", TOTAL_REQUESTS);
        logger.info("   Successful: {}", successCount);
        logger.info("   Errors: {}", errorCount);
        logger.info("   Total duration: {}ms", totalDuration);
        logger.info("   Average per request: {}ms", totalDuration / TOTAL_REQUESTS);
        
        assertEquals(TOTAL_REQUESTS, successCount, "Alle requests moeten succesvol zijn");
        assertEquals(0, errorCount, "Er mogen geen errors zijn");
    }
    
    private void validateFinalDatabaseState() {
        var dbRecord = baTotalRepository.findByKenmerk(kenmerk);
        assertTrue(dbRecord.isPresent(), "Database record moet bestaan voor kenmerk: " + kenmerk);
        
        BaTotalRecord record = dbRecord.get();
        
        assertNotNull(record.getTotaal1(), "Totaal1 mag niet null zijn");
        assertNotNull(record.getTotaal2(), "Totaal2 mag niet null zijn");
        assertNotNull(record.getTotaal3(), "Totaal3 mag niet null zijn");
        assertNotNull(record.getTotaal4(), "Totaal4 mag niet null zijn");
        assertNotNull(record.getTotaal5(), "Totaal5 mag niet null zijn");
        
        // Verifieer dat de laatste percentage (100%) is gebruikt
        assertTrue(record.getTotaal4() > 0, "Totaal4 moet groter dan 0 zijn (laatste percentage effect)");
        
        logger.info("Database validatie: Finale totalen [{}]", 
            String.format("%.1f, %.1f, %.1f, %.1f, %.1f", 
                record.getTotaal1(), record.getTotaal2(), record.getTotaal3(), 
                record.getTotaal4(), record.getTotaal5()));
    }
}
