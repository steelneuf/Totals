package com.totals.controller;

import com.totals.dto.BaLogsResponse;
import com.totals.service.logging.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@RestController
@RequestMapping("/api/${api.version}/ba/{kenmerk}/logs")
@Validated
public class LogsController {
    
    private static final Logger logger = LoggerFactory.getLogger(LogsController.class);
    
    private final LogService logService;
    
    public LogsController(LogService logService) {
        this.logService = logService;
    }
    
    @GetMapping
    public ResponseEntity<BaLogsResponse> getLogsForKenmerk(
            @PathVariable @NotBlank @Pattern(regexp = "^[a-zA-Z0-9_-]+$") String kenmerk,
            @RequestParam(defaultValue = "${api.validation.logs-default-entries:50}") 
            @Min(value = 1, message = "Aantal entries moet minimaal 1 zijn") 
            @Max(value = 1000, message = "Aantal entries mag maximaal 1000 zijn") int last) throws java.io.IOException {
        
        var logEntries = logService.getLogsForKenmerk(kenmerk, last);
        
        logger.info("Logs opgehaald voor kenmerk={} (last: {})", kenmerk, last);
        
        return ResponseEntity.ok(BaLogsResponse.success(kenmerk, logEntries));
    }
}
