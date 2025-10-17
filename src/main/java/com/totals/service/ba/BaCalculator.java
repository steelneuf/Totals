package com.totals.service.ba;

import com.totals.dto.BaTotalsResponse;
import com.totals.model.BaTotalRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BaCalculator {
    
    private static final Logger logger = LoggerFactory.getLogger(BaCalculator.class);
    
    public BaTotalRecord performCalculations(BaTotalsResponse agg, Integer percentage, String kenmerk) {
        try {
            double bijdrage = percentage / 100.0;
            double cijfer7 = agg.totaal1();
            double cijfer1 = calculateCijfer1(agg.totaal1(), agg.totaal2(), bijdrage);
            
            double totaal1 = cijfer1;
            double totaal2 = (agg.totaal3() / cijfer7) * cijfer1;
            double totaal3 = (agg.totaal4() / cijfer7) * cijfer1;
            double totaal4 = agg.totaal5() * bijdrage;
            double totaal5 = agg.totaal6();
            
            BaTotalRecord result = new BaTotalRecord();
            result.setTotaal1(totaal1);
            result.setTotaal2(totaal2);
            result.setTotaal3(totaal3);
            result.setTotaal4(totaal4);
            result.setTotaal5(totaal5);
            return result;
        } catch (Exception e) {
            logger.error("Berekening mislukt: kenmerk={} fout={}", kenmerk, e.getMessage());
            throw e;
        }
    }
    
    public double calculateCijfer1(double totaal1, double totaal2, double bijdrage) {
        double threshold = totaal2 / bijdrage;
        return totaal1 > threshold ? threshold : totaal1;
    }
}
