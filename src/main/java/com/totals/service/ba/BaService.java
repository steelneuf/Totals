package com.totals.service.ba;

import com.totals.dto.BaTotalsResponse;
import com.totals.model.BaTotalRecord;
import com.totals.repository.BaRecordRepository;
import com.totals.repository.BaTotalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BaService {
    
    private static final Logger logger = LoggerFactory.getLogger(BaService.class);
    
    private final BaRecordRepository recordRepository;
    private final BaTotalRepository totalRepository;
    private final BaValidator baValidator;
    private final BaCalculator baCalculator;
    
    public BaService(BaRecordRepository recordRepository, BaTotalRepository totalRepository, 
                     BaValidator baValidator, BaCalculator baCalculator) {
        this.recordRepository = recordRepository;
        this.totalRepository = totalRepository;
        this.baValidator = baValidator;
        this.baCalculator = baCalculator;
    }
    
    @Transactional
    public BaTotalRecord calculateTotals(String kenmerk, Integer bijdragePercentage, LocalDateTime requestTime) {
        baValidator.validateInputs(kenmerk, bijdragePercentage);
        
        BaTotalsResponse totalsResponse = recordRepository.calculateTotalsByKenmerk(kenmerk)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Geen records voor: " + kenmerk));
        baValidator.validateDivisor(totalsResponse.totaal1(), kenmerk);
        
        BaTotalRecord calculatedTotal = baCalculator.performCalculations(totalsResponse, bijdragePercentage, kenmerk);
        return saveOrUpdateTotal(kenmerk, calculatedTotal, requestTime);
    }
    
    @Transactional
    public BaTotalRecord saveOrUpdateTotal(String kenmerk, BaTotalRecord calculated, LocalDateTime requestTime) {
        Long requestTimestamp = requestTime.toInstant(java.time.ZoneOffset.UTC).toEpochMilli();
        
        // Probeer eerst te updaten
        int updatedRows = totalRepository.updateIfNewer(kenmerk, 
            calculated.getTotaal1(), calculated.getTotaal2(), calculated.getTotaal3(), 
            calculated.getTotaal4(), calculated.getTotaal5(), requestTimestamp);
        
        if (updatedRows > 0) {
            calculated.setRequestTimestamp(requestTimestamp);
            logger.info("Berekening gelukt: kenmerk={} opgeslagen-op={} totalen={}", 
                kenmerk, "update", List.of(calculated.getTotaal1(), calculated.getTotaal2(), 
                calculated.getTotaal3(), calculated.getTotaal4(), calculated.getTotaal5()));
            return calculated;
        } else {
            // Als update niet lukte, probeer insert of return existing
            BaTotalRecord result = totalRepository.findByKenmerk(kenmerk)
                .orElseGet(() -> {
                    BaTotalRecord newRecord = new BaTotalRecord(kenmerk, calculated);
                    newRecord.setRequestTimestamp(requestTimestamp);
                    return totalRepository.save(newRecord);
                });
            logger.info("Berekening gelukt: kenmerk={} opgeslagen-op={} totalen={}", 
                kenmerk, "insert/existing", List.of(result.getTotaal1(), result.getTotaal2(), 
                result.getTotaal3(), result.getTotaal4(), result.getTotaal5()));
            return result;
        }
    }
}
