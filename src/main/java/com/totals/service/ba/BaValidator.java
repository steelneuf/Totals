package com.totals.service.ba;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Component
public class BaValidator {
    
    private static final Logger logger = LoggerFactory.getLogger(BaValidator.class);
    
    public void validateInputs(String kenmerk, Integer bijdragePercentage) {
        if (kenmerk == null || kenmerk.trim().isEmpty()) {
            logger.warn("Validatie mislukt kenmerk={} fout={}", kenmerk, "Kenmerk mag niet leeg zijn");
            throw new IllegalArgumentException("Kenmerk mag niet leeg zijn");
        }
        if (kenmerk.length() < 1 || kenmerk.length() > 50) {
            logger.warn("Validatie mislukt kenmerk={} fout={}", kenmerk, "Kenmerk lengte moet tussen 1 en 50 karakters zijn");
            throw new IllegalArgumentException("Kenmerk lengte moet tussen 1 en 50 karakters zijn");
        }
        if (!kenmerk.matches("^[a-zA-Z0-9_-]+$")) {
            logger.warn("Validatie mislukt kenmerk={} fout={}", kenmerk, "Kenmerk mag alleen letters, cijfers, underscores en streepjes bevatten");
            throw new IllegalArgumentException("Kenmerk mag alleen letters, cijfers, underscores en streepjes bevatten");
        }
        
        if (bijdragePercentage == null) {
            logger.warn("Validatie mislukt kenmerk={} fout={}", kenmerk, "Bijdragepercentage mag niet leeg zijn");
            throw new IllegalArgumentException("Bijdragepercentage mag niet leeg zijn");
        }
        if (bijdragePercentage < 1 || bijdragePercentage > 100) {
            logger.warn("Validatie mislukt kenmerk={} fout={}", kenmerk, "Bijdragepercentage moet tussen 1 en 100 zijn");
            throw new IllegalArgumentException("Bijdragepercentage moet tussen 1 en 100 zijn");
        }
    }
    
    public void validateDivisor(double value, String kenmerk) {
        if (value == 0.0) {
            logger.warn("Validatie mislukt kenmerk={} fout={}", kenmerk, "Berekening niet mogelijk: totaal1 is 0, wat leidt tot division by zero");
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, 
                "Berekening niet mogelijk: totaal1 is 0, wat leidt tot division by zero");
        }
    }
}
