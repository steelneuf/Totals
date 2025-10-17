package com.totals.controller;

import com.totals.dto.BaCalculationResponse;
import com.totals.model.BaTotalRecord;
import com.totals.service.ba.BaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/${api.version}")
public class BaController {
    
    private static final Logger logger = LoggerFactory.getLogger(BaController.class);
    
    private final BaService baService;
    
    public BaController(BaService baService) {
        this.baService = baService;
    }
    
    @PostMapping("/ba/{kenmerk}/calculate-totals")
    public ResponseEntity<BaCalculationResponse> calculateTotals(
            @PathVariable String kenmerk,
            @RequestParam Integer bijdragePercentage) {
        
        logger.info("POST /api/v1/ba/{}/calculate-totals berekening ontvangen payload={{kenmerk={}, bijdragePercentage={}}}", 
            kenmerk, kenmerk, bijdragePercentage);
        
        long startTime = System.currentTimeMillis();
        LocalDateTime requestTime = LocalDateTime.now();
        
        BaTotalRecord total = baService.calculateTotals(kenmerk, bijdragePercentage, requestTime);
        List<Double> totalen = List.of(
            total.getTotaal1(), 
            total.getTotaal2(), 
            total.getTotaal3(), 
            total.getTotaal4(), 
            total.getTotaal5()
        );
        
        long duration = System.currentTimeMillis() - startTime;
        
        logger.info("POST /api/v1/ba/{}/calculate-totals response verstuurd in {}ms", kenmerk, duration);
        
        return ResponseEntity.ok(BaCalculationResponse.success(kenmerk, bijdragePercentage, totalen, duration));
    }
    
}
